package gov.csr.generic.batch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "BATCH_JOB_CONFIG")
@Setter
@Getter
@NoArgsConstructor
public class BatchJobConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "JOB_NAME", nullable = false, unique = true)
    private String jobName;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "CLIENT_SECRET")
    private String clientSecret;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "IS_BEARER", nullable = false, columnDefinition = "NUMBER(1,0)")
    private boolean bearer;

    @Enumerated(EnumType.STRING)
    @Column(name = "BATCH_STATUS", nullable = false)
    private BatchStatus batchStatus;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "IS_RUNNING", nullable = false, columnDefinition = "NUMBER(1,0)")
    private boolean running = false;

    @Column(name = "LAST_START_TIME")
    private LocalDateTime lastStartTime;

    @Column(name = "LAST_FINISH_TIME")
    private LocalDateTime lastFinishTime;

    @Column(name = "CHUNK_SIZE", nullable = false)
    private Integer chunkSize;

    @Column(name = "PAGE_SIZE", nullable = false)
    private Integer pageSize;

    @Column(name = "THREAD_COUNT", nullable = false)
    private Integer threadCount;

    @Column(name = "SLEEP_TIME_MS", nullable = false)
    private Long sleepTimeMs;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "NEED_RESTART", nullable = false, columnDefinition = "NUMBER(1,0)")
    private boolean needRestart = false;
}
