# SPL Batch Integration — Technical Handover Report

**Project:** GENERIC-BATCH  
**Module:** SPL Integration  
**Branch:** `feature/stored-procedure-partitioning`  
**Status:** `BLOCKED — Pending Business Requirements and Technical Specifications`  
**Implementation baseline reviewed:** `3acdbf9`

## 1. Purpose and Expected Workflow

The objective is to process the eligible source records through the SPL integration library using Spring Batch partitioning and bounded multithreading.

The intended workflow is:

1. Load the SPL job configuration.
2. Read the configured Stored Procedure name from `BATCH_JOB_CONFIG.PROCEDURE_NAME`.
3. Execute that procedure once per job execution, without input parameters, as agreed.
4. After the procedure completes, identify the eligible source records and calculate their total count.
5. Divide the eligible records into balanced, deterministic, non-overlapping partitions.
6. Process the partitions concurrently, with the number of workers controlled by `THREAD_COUNT`.
7. Read each partition's assigned records and invoke the SPL library for each `SOCIAL_ID`.
8. Persist each processing outcome and update record status according to the approved business rules.

## 2. What Is Present in the Current Branch

The following SPL-related foundation is present in the branch at the implementation baseline:

- **`SPLStoredProcedureExecutor`** — loads the job configuration through `JobConfigService`, resolves `PROCEDURE_NAME`, validates the procedure identifier, and invokes it through Spring JDBC with no input parameters.
- **`SPLProcedureTasklet`** — exposes the procedure execution as a Spring Batch tasklet.
- **`SPLTaskExecutorFactory`** — creates a fixed-size `ThreadPoolTaskExecutor` using the configured thread count, with a zero-capacity queue and a job-specific thread-name prefix.
- **`SPLTaskExecutorShutdownListener`** — shuts down the worker pool after the job execution.
- **`SPLPartitionedJobFactory`** — provides a procedure step followed by a partitioned master/worker step, with chunk transactions configured for the worker step.
- **`SPLDynamicIdRangePartitioner` / `SPLIdRangePartitioner`** — provide ID-range partitioning infrastructure.
- **`SPLAddressItemProcessor`** — invokes the SPL library for a source person and maps successful, empty, no-record-found, and technical-failure outcomes to the SPL result model.
- Initial unit tests exist for several SPL components and partitioning utilities.

The existing `BatchJobConfig` entity maps `PROCEDURE_NAME`, `THREAD_COUNT`, `CHUNK_SIZE`, and `PAGE_SIZE`.

## 3. Important Implementation Gaps

The current branch is a foundation, not a completed end-to-end SPL batch job. The following gaps were confirmed during code review:

1. **No source-table configuration is currently mapped in `BatchJobConfig`.** There is no `TABLE_NAME` field in the entity shown in the current branch. The approved location and format for supplying the source table name must be defined before a dynamic table-based reader or count query can be implemented.
2. **No concrete source COUNT query exists.** `SPLIdRangeProvider` is an interface; there is no implementation that queries the actual source data.
3. **Count-based partitioning is not implemented.** The current partitioner divides a numeric inclusive ID range. This is not equivalent to distributing an exact number of eligible rows evenly, especially when IDs contain gaps.
4. **No SPL source `ItemReader` is implemented.** There is no database query that retrieves the source records assigned to each worker.
5. **No SPL result `ItemWriter` is implemented.** The destination table, persistence mapping, and update rules are not defined in code.
6. **The SPL master/worker factory accepts its partitioner, reader, processor, and writer as inputs.** The actual source-specific components are not wired into a complete SPL job configuration.
7. **The current `JobResolver` delegates to `GenericJobFactory`.** The SPL partitioned job factory is not yet connected to the dynamic job-resolution path.
8. **Production-level concurrency and restart behavior have not been validated** against the actual source and target database schema and business rules.

The current partitioner should therefore not be described as the requested count-based distribution, and the current branch should not be described as production-ready.

## 4. Information Required from the Business Owner

Development cannot safely continue until the following information is supplied and confirmed.

### A. Source Table and Runtime Configuration

- Exact source table or view name.
- Database schema/owner.
- Confirmation that this table contains the records to be submitted to SPL after the preparation procedure runs.
- Where the table name will be configured at runtime. The current `BatchJobConfig` entity does not contain a `TABLE_NAME` mapping.

### B. Stored Procedure

- Exact procedure name and its schema/package, if applicable.
- Confirmation that it accepts no input parameters.
- Its business purpose and its relationship to the source table.
- Confirmation that it should run once at the beginning of each SPL job execution.
- Confirmation of its transaction/commit behavior and whether it prepares, inserts, updates, or deletes source records.

The procedure name can be supplied through the existing `BATCH_JOB_CONFIG.PROCEDURE_NAME` field.

### C. Record Identification and Selection

- Primary key or another unique record identifier.
- Stable ordering key suitable for deterministic partitioning.
- Exact column containing `SOCIAL_ID`.
- Selection criteria defining which records are eligible.
- How already-processed records are identified and excluded, if applicable.
- Whether failed records should be retried and how they are selected.

### D. Expected Processing Output

- Target table or other destination for SPL results.
- Required target columns and their mapping from the SPL response.
- Whether one source person can produce multiple address rows and how those rows must be stored.
- Required processing statuses, including success, empty result, no record found, and technical failure.
- Required error-message storage and source-record update behavior.

### E. Concurrency, Consistency, and Recovery

- Maximum permitted thread count and expected source data volume.
- Database connection limits and any SPL service rate limits.
- Confirmation that separate source records may safely be processed concurrently.
- Required transaction and locking behavior.
- Whether the source dataset must remain stable during the job, or whether a database snapshot/high-water mark is required.
- Required restart, retry, and idempotency behavior after partial failure.

## 5. Proposed Count-Based Partitioning Design

Once the source table, selection criteria, and unique ordering key are confirmed, the intended design is:

1. Execute the configured preparation procedure.
2. Run a count query using the exact same eligibility predicate that the workers will use.
3. Divide the total eligible row count into up to `THREAD_COUNT` balanced partitions.
4. Assign each partition a deterministic, non-overlapping slice of the ordered eligible dataset.
5. Have each worker read only its assigned slice, using the configured `PAGE_SIZE`.
6. Process records through the SPL library.
7. Persist outcomes in chunks using the configured `CHUNK_SIZE`.

A stable unique ordering key and a consistent view of the source dataset are required to prevent records from being skipped or processed more than once if the source changes while the job is running. The exact SQL strategy must be selected after the Oracle schema and business selection rules are supplied.

## 6. Reasons Development Has Been Stopped

Development has been stopped at the current implementation stage for the following reasons:

1. The actual source table and its schema have not been provided.
2. The unique record key, `SOCIAL_ID` column, and source selection criteria have not been specified.
3. The required runtime configuration mechanism for the source table name has not been confirmed.
4. The business purpose and expected effect of the stored procedure on the source dataset have not been fully specified.
5. The destination for SPL results and the required persistence/status-update rules have not been defined.
6. Error, retry, restart, and idempotency requirements have not been agreed.
7. The database concurrency, transaction, and data-consistency requirements are unknown.

Implementing the COUNT query, source reader, count-based partitioner, and writer without these inputs would require assumptions about database objects and business behavior. Such assumptions could lead to incorrect record selection, duplicate or missing processing, data conflicts, or results that do not meet the actual business requirements.

## 7. Risks of Proceeding Without the Required Information

- Processing records outside the intended business scope.
- Missing eligible records or processing records more than once.
- Uneven partition workloads and unreliable performance.
- Concurrent update conflicts or database locking issues.
- Incorrect or incomplete persistence of SPL responses.
- Unreliable restart and retry behavior.
- Inability to verify the solution against the actual business process or approve it for production.

## 8. Acceptance Criteria for Resuming and Closing the Task

Before the task can be considered complete and production-ready, the following must be demonstrated:

- The configured procedure is executed exactly as agreed and without input parameters.
- The source count and worker selection use identical eligibility criteria.
- All eligible records are covered exactly once by the partitions.
- Partition sizes are balanced according to the approved strategy.
- Worker concurrency never exceeds the configured limit.
- Chunk and page sizes are respected.
- SPL results and all required statuses are persisted correctly.
- Error handling, retry, restart, and idempotency are verified.
- Concurrent execution is tested against the actual database.
- Integration and performance tests pass.

## 9. Final Status and Required Next Action

The SPL integration foundation and initial master/worker infrastructure are present in the current branch. However, the source-specific reader, count query, count-based partitioning, writer, and dynamic job-resolution integration remain incomplete.

**Development is being stopped at this point because the required business information and technical specifications have not been provided in a clear, confirmed form.** Continuing beyond this point would require unverified assumptions about the database schema, source records, business selection rules, and result persistence.

The task can be resumed once the business owner provides and confirms the information listed in Section 4.

**Final Status: BLOCKED — PENDING BUSINESS REQUIREMENTS AND TECHNICAL SPECIFICATIONS.**

**Validation note:** This handover commit adds documentation only. It does not change application code. Maven tests were not executed as part of preparing this report.
