# Java Code Review — generic-batch

## Scope

- التاريخ: 2026-11-10. النطاق: الخدمة كاملة (`src/main/java`, `src/main/resources`, ملفات النشر المرتبطة، `pom.xml`، والاختبارات الثلاثة). تتبعت Scheduler → Launcher → JobFactory → Partitioner/Reader → NIC Processor → Writer → listeners/restart.
- المراجعة على حالة الملفات الحالية، بما فيها التعديلات المحلية غير الملتزم بها في `pom.xml` و`GenericJobFactory.java` و`PersonBasicInfoMapper.java` والاختبار. لم أعدل كود الخدمة أو أتصل بقاعدة Oracle/NIC.
- درجات التأكد تفصل السلوك المثبت من الكود عن المخاطر التي تعتمد على بيئة النشر.

## Summary

**13 نتيجة: 2 Blocker، 6 High، 5 Medium.** أخطرها خطأ اسم إعداد الـ restart scheduler، وفقد سجلات بسبب إعادة حساب `ROW_NUMBER` بعد تغيير `IS_UPDATED` أثناء الـ Job.

## Findings

### [Blocker] اسم خاصية cron للـ restart لا يطابق `application.yml`
- **Location:** `src/main/java/gov/csr/generic/batch/scheduler/BatchRestartScheduler.java:21`; `src/main/resources/application.yml:40`
- **Category:** Bug
- **Confidence:** High، ما لم تُعرّف الخاصية المفقودة خارجيًا

**Problem:** الكود يطلب `${generic-batch-restart.scheduler-cron}`، بينما YAML يعرّف `generic-batch.restart.scheduler-cron`. هما اسمان مختلفان؛ مع الإعدادات الموجودة فقط، يفشل إنشاء مهمة `@Scheduled` عند بدء التطبيق بسبب placeholder غير محلول، فلا يبدأ التطبيق.

**Suggested fix:** غيّر الـ placeholder إلى `${generic-batch.restart.scheduler-cron}` أو وحّد اسم المفتاح في YAML، مع اختبار context startup يحمّل الإعدادات الفعلية.

### [Blocker] تقسيم الصفوف بالـ `ROW_NUMBER` يفوّت صفوفًا أثناء الحفظ
- **Location:** `src/main/java/gov/csr/generic/batch/reader/PersonBasicInfoReaderConfig.java:66`; `src/main/java/gov/csr/generic/batch/util/PersonBasicInfoTablePartitioner.java:32`; `src/main/java/gov/csr/generic/batch/procrssor/PersonBasicInfoProcessor.java:59`
- **Category:** Bug
- **Confidence:** High

**Problem:** الـ partitioner يحسب عدد صفوف `IS_UPDATED=0` ويوزع **ترتيبها** إلى `startRow/endRow`. الـ reader يحسب `ROW_NUMBER()` من جديد على نفس المجموعة، بينما الـ processor يغيّر `IS_UPDATED` إلى 1/2/3 ويحفظه writer. بعد أول commit، تختفي الصفوف المنجزة من المجموعة فتتغير أرقام الصفوف الباقية؛ partition لاحقة أو الصفحة التالية قد لا تراها. مثال محلي بستة صفوف وpartitions `1..3` و`4..6`: بعد تحديث الثلاثة الأولى، صفوف 4..6 تصير أرقامها `1..3`، فتقرأ partition الثانية صفرًا رغم بقائها غير معالجة. الاختبار الحالي للـ partitioner يفحص حدودًا حسابية فقط ولا يختبر تغير البيانات.

**Suggested fix:** ثبّت مجموعة العمل قبل التنفيذ (جدول IDs مع `run_id` أو claim/lease)، أو قسم بنطاقات **ID ثابتة** واقرأ `WHERE ID BETWEEN ...` مع ترتيب ID، ولا تعتمد على ordinal لصفوف يتغير شرط أهليتها. اختبر تشغيلًا بصفوف أكثر من chunk واحد ومع 2+ partitions بعد commit فعلي.

### [High] أخطاء NIC المؤقتة تُحفظ كحالة نهائية فلا تُعاد المحاولة
- **Location:** `src/main/java/gov/csr/generic/batch/procrssor/PersonBasicInfoProcessor.java:44`; `src/main/java/gov/csr/generic/batch/reader/PersonBasicInfoReaderConfig.java:69`
- **Category:** Bug
- **Confidence:** High

**Problem:** عند `ServiceUnavailableException` يضع processor `IS_UPDATED=3` ويرجع entity إلى writer؛ الـ step يعتبرها معالجة، وقراءة التشغيلات التالية تشترط `IS_UPDATED=0`. بذلك انقطاع NIC المؤقت يمنع الصف من إعادة المحاولة تلقائيًا. حتى الرد الفارغ يسجل `2` ويخرج من مجموعة القراءة.

**Suggested fix:** افصل حالة الفشل المؤقت عن الفشل الدائم؛ استخدم `faultTolerant().retry(...)` بحد وbackoff للانقطاع، وسجل محاولة/موعد إعادة المعالجة أو أعد status إلى pending وفق السياسة. لا تجعل نقص الرد النهائي إلا إذا كان عقد NIC يثبت أنه دائم. اختبر فشلًا مؤقتًا ثم نجاحًا.

### [High] زر restart يبدأ JobInstance جديدة ويمسح الطلب قبل نجاح التشغيل
- **Location:** `src/main/java/gov/csr/generic/batch/scheduler/BatchRestartScheduler.java:29`; `src/main/java/gov/csr/generic/batch/service/DynamicBatchLauncher.java:45`
- **Category:** Bug
- **Confidence:** High

**Problem:** scheduler يمسح `needRestart` ويحفظه قبل `jobLauncher.run`، ثم launcher ينشئ `run.id` جديدًا. هذا **تشغيل جديد** وليس restart للـ JobInstance المتوقفة من آخر chunk. لو launcher فشل، طلب restart ضاع. ولو نجح، مدى العمل يُحسب من جديد، ومع مشكلة `ROW_NUMBER` قد تُفوت صفوف أو تتكرر معالجتها.

**Suggested fix:** احتفظ بمعلمات الـ JobInstance الأصلية وأعد تشغيلها عبر آلية Spring Batch بعد stop/failed، أو صمّم تشغيلًا جديدًا idempotent بوضوح. لا تمسح flag إلا بعد تأكيد قبول التشغيل وتسجيل الحالة، وأعده عند الفشل. اختبر stop منتصف Job ثم restart.

### [High] حماية عدم تشغيل نفس الـ Job مرتين غير ذرّية بين نسخ الخدمة
- **Location:** `src/main/java/gov/csr/generic/batch/service/DynamicBatchLauncher.java:39`; `src/main/java/gov/csr/generic/batch/listener/BatchJobRunningListener.java:24`
- **Category:** Bug
- **Confidence:** Medium؛ يظهر عند تشغيل أكثر من instance أو تشغيلين متزامنين

**Problem:** `findRunningJobExecutions` ثم `run` عمليتان منفصلتان، و`run.id` مختلف لكل إطلاق. في نسختين من الخدمة يمكن أن تريا عدم وجود Job شغالة معًا، ثم يمر فحص `config.isRunning()` قبل أن تحفظ أي منهما `true`. النتيجة المحتملة Jobان على نفس صفوف NIC/Oracle.

**Suggested fix:** استخدم lock موزع/claim ذري في DB مع قيد يمنع أكثر من تشغيل نشط، أو مفتاح JobInstance ثابت لكل دورة عمل بحيث يمنع JobRepository الإطلاق المكرر. اختبر إطلاقين متزامنين بقاعدة مشتركة.

### [High] كل تشغيل ينشئ thread pools جديدة ولا يغلقها
- **Location:** `src/main/java/gov/csr/generic/batch/config/GenericJobFactory.java:107`; `src/main/java/gov/csr/generic/batch/util/Utils.java:12`
- **Category:** Bug
- **Confidence:** High

**Problem:** `buildJob` يستدعي `Utils.taskExecutor(threadCount)` لمرحلتَي HIGH وLOW عند كل تشغيل. الدالة تنشئ `ThreadPoolTaskExecutor` وتعمل `initialize()` من دون Bean lifecycle أو `shutdown()`. مع scheduler دوري، تبقى threads/resources لكل Job بعد انتهائها وتتراكم بمرور الوقت.

**Suggested fix:** أنشئ executor مُدارًا من Spring وأعد استخدامه بحد مناسب، أو أغلق executor المخصص بعد اكتمال الـ Job في `finally`/listener يملك دورة حياته. راقب عدد threads بعد تشغيلات متكررة.

### [High] restart scheduler يتجاهل حالة `DISABLED`
- **Location:** `src/main/java/gov/csr/generic/batch/scheduler/BatchRestartScheduler.java:24`; `src/main/java/gov/csr/generic/batch/service/DynamicBatchLauncher.java:36`
- **Category:** Bug
- **Confidence:** High

**Problem:** الـ scheduler العادي يمنع Job معطلة في `src/main/java/gov/csr/generic/batch/scheduler/BatchScheduler.java:30`، لكن restart scheduler يختار أي `needRestart=true && running=false` ويشغلها دون فحص `batchStatus`. يمكن تشغيل Job أوقفها المشغل عمدًا.

**Suggested fix:** طبّق شرط `BatchStatus.ENABLED` عند اختيار restart، وأعد فحصه داخل launcher نفسه حتى لا يوجد مدخل يتجاوز السياسة. اختبر `DISABLED + needRestart=true`.

### [High] أسرار قاعدة البيانات والـ registry موجودة مباشرة في المصدر
- **Location:** `src/main/resources/application.yml:5`; `pom.xml:194`
- **Category:** Bug
- **Confidence:** High

**Problem:** كلمة مرور Oracle وكلمة مرور registry قيم مباشرة في ملفات المشروع؛ أي نسخة من الكود أو build artifact قد تكشفها. لم أكرر القيم في التقرير.

**Suggested fix:** انقلها إلى secret manager/بيئة التشغيل، ودوّر credentials الحالية واحذفها من تاريخ Git حسب سياسة الفريق. افحص الـ artifact والـ logs بعد التغيير.

### [Medium] صورة Jib Java 11 لا تشغّل bytecode Java 17
- **Location:** `pom.xml:17`; `pom.xml:191`
- **Category:** Bug
- **Confidence:** High، إذا كان النشر بـ Jib

**Problem:** المشروع يترجم لـ Java 17 بينما Jib base image هي Java 11؛ الصورة الناتجة ستفشل بـ `UnsupportedClassVersionError` عند التشغيل.

**Suggested fix:** استعمل base image Java 17 أو أعلى ومتوافقة مع سياسة التشغيل، واختبر تشغيل الصورة المبنية.

### [Medium] `running` قد تظل true بعد تعطل العملية وتمنع التعافي
- **Location:** `src/main/java/gov/csr/generic/batch/listener/BatchJobRunningListener.java:28`; `src/main/java/gov/csr/generic/batch/scheduler/BatchRestartScheduler.java:24`
- **Category:** Bug
- **Confidence:** Medium؛ يتطلب crash/kill قبل `afterJob`

**Problem:** `beforeJob` يضع `running=true` ولا يعيدها false إلا `afterJob`. إذا توقف JVM فجأة فلن ينفذ callback؛ restart scheduler يطلب `running=false` فلا يلتقط الـ Job، وlistener سيرفض تشغيلًا جديدًا لاحقًا.

**Suggested fix:** اجعل حالة التشغيل مشتقة من Spring Batch metadata مع سياسة recovery للـ executions اليتيمة، أو استخدم lease له انتهاء/heartbeat وclear آمن عند startup بعد فحص الحيازة. اختبر kill أثناء التنفيذ ثم إعادة تشغيل الخدمة.

### [Medium] إعدادات chunk/page/threads من DB بلا حدود تحقق
- **Location:** `src/main/java/gov/csr/generic/batch/entity/BatchJobConfig.java:46`; `src/main/java/gov/csr/generic/batch/config/GenericJobFactory.java:38`; `src/main/java/gov/csr/generic/batch/reader/PersonBasicInfoReaderConfig.java:100`
- **Category:** Bug
- **Confidence:** High

**Problem:** `threadCount`, `chunkSize`, `pageSize` من الجدول تُمرر مباشرة للـ executor والـ Step والـ reader. `0` أو قيمة سالبة/كبيرة جدًا قد تمنع بناء Job أو تستنفد موارد الخدمة. لا يوجد تحقق في entity أو `JobConfigService`.

**Suggested fix:** تحقق عند تحميل config: قيم موجبة وحدود قصوى مناسبة لـ Oracle/NIC/Hikari، مع رسالة خطأ واضحة ورفض إطلاق Job غير صالح. اختبر صفرًا وسالبًا وقيمة تتجاوز الحد.

### [Medium] إعدادات بدء التشغيل تسمح بتغيير schema تلقائيًا
- **Location:** `src/main/resources/application.yml:16`; `src/main/resources/application.yml:25`
- **Category:** Improvement
- **Confidence:** High للإعداد الحالي؛ الأثر يعتمد على صلاحيات قاعدة الإنتاج

**Problem:** `spring.jpa.hibernate.ddl-auto=update` و`spring.batch.jdbc.initialize-schema=always` قد يحاولان تعديل جداول Oracle/Batch عند كل إقلاع. هذا يصعّب ضبط migrations وقد يمنع الإقلاع إذا كانت DDL غير مسموحة أو الجداول موجودة.

**Suggested fix:** في الإنتاج استخدم `validate`/`none` و`initialize-schema=never`، وانقل تغييرات schema إلى migrations مُراجعة ومُرقمة. اختبر الإقلاع على schema موجودة مسبقًا.

### [Medium] كل طلب NIC يقرأ إعدادات credentials من Oracle
- **Location:** `src/main/java/gov/csr/generic/batch/config/BearerAuthInterceptor.java:20`; `src/main/java/gov/csr/generic/batch/procrssor/PersonBasicInfoProcessor.java:33`
- **Category:** Improvement
- **Confidence:** High

**Problem:** `RequestInterceptor.apply` ينفذ `findByJobName` لكل طلب Feign، والـ processor يرسل طلبًا لكل صف. في Batch كبير، هذا يضيف استعلام DB لكل مواطن ويزيد ضغط pool ويجعل استدعاء NIC يعتمد على توفر DB مرة أخرى.

**Suggested fix:** حمّل credentials مرة لكل تشغيل أو استخدم cache قصيرة العمر مع آلية تحديث/إبطال واضحة عند تدوير السر. اختبر عدد استعلامات config عند معالجة 100 صف.

## Positive observations

- فصل HIGH عن LOW في `GenericJobFactory` واضح، و`@StepScope` على readers/writer يمنح كل partition سياق تنفيذ مناسب.
- mapper الحالي يتجاهل `socialId` عند تحديث response، ويوجد اختبار يثبت الحفاظ عليه.
- الاختبارات الثلاثة المتاحة تمر على Java 21.

## Validation

- `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn -o -q test` نجح: اختبارات mapper وlauncher وpartitioner.
- `mvn -o -q -DskipTests compile` على Java 25 فشل بسبب توافق Lombok/Javac (`TypeTag :: UNKNOWN`). لذلك استخدمت Java 21 المتاحة للتحقق.
- أعدت إنتاج مشكلة تحرك `ROW_NUMBER` بتجربة SQL صغيرة في SQLite: بعد تحديث أول 3 من 6 صفوف، النطاق `4..6` صار فارغًا رغم بقاء 3 صفوف pending. هذه إعادة إنتاج للمنطق، وليست اختبارًا على Oracle الحقيقي.
- لم أبدأ التطبيق ولم أرسل طلبات NIC ولم أستخدم قاعدة Oracle؛ لا توجد اختبارات integration للـ Job الكامل أو restart/concurrency.
