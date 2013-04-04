package erwins.util.vender.springBatch;

import static org.springframework.batch.support.DatabaseType.SYBASE;

import java.sql.Types;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JdbcExecutionContextDao;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;
import org.springframework.batch.core.repository.dao.JdbcStepExecutionDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.AbstractJobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * 확장 포인트를 알기 힘들어서 걍 다 복사했다. 
 * 팩토리니깐 머 괜찮겠지.. ㅋㅋ
 * 기존 DAO를 JdbcExecutionContextDaoForTempData 로 교체
 * @author sin
 */
public class JobRepositoryFactoryBeanForTempData extends AbstractJobRepositoryFactoryBean{
    
    protected static final Log logger = LogFactory.getLog(JobRepositoryFactoryBean.class);

    private DataSource dataSource;

    private SimpleJdbcOperations jdbcTemplate;

    private String databaseType;

    private String tablePrefix = AbstractJdbcBatchMetadataDao.DEFAULT_TABLE_PREFIX;

    private DataFieldMaxValueIncrementerFactory incrementerFactory;

    private int maxVarCharLength = AbstractJdbcBatchMetadataDao.DEFAULT_EXIT_MESSAGE_LENGTH;

    private LobHandler lobHandler;

    /**
     * A special handler for large objects. The default is usually fine, except
     * for some (usually older) versions of Oracle. The default is determined
     * from the data base type.
     * 
     * @param lobHandler the {@link LobHandler} to set
     * 
     * @see LobHandler
     */
    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }

    /**
     * Public setter for the length of long string columns in database. Do not
     * set this if you haven't modified the schema. Note this value will be used
     * for the exit message in both {@link JdbcJobExecutionDao} and
     * {@link JdbcStepExecutionDao} and also the short version of the execution
     * context in {@link JdbcExecutionContextDao} . For databases with
     * multi-byte character sets this number can be smaller (by up to a factor
     * of 2 for 2-byte characters) than the declaration of the column length in
     * the DDL for the tables.
     * 
     * @param maxVarCharLength the exitMessageLength to set
     */
    public void setMaxVarCharLength(int maxVarCharLength) {
        this.maxVarCharLength = maxVarCharLength;
    }

    /**
     * Public setter for the {@link DataSource}.
     * @param dataSource a {@link DataSource}
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Sets the database type.
     * @param dbType as specified by
     * {@link DefaultDataFieldMaxValueIncrementerFactory}
     */
    public void setDatabaseType(String dbType) {
        this.databaseType = dbType;
    }

    /**
     * Sets the table prefix for all the batch meta-data tables.
     * @param tablePrefix
     */
    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public void setIncrementerFactory(DataFieldMaxValueIncrementerFactory incrementerFactory) {
        this.incrementerFactory = incrementerFactory;
    }

    public void afterPropertiesSet() throws Exception {

        Assert.notNull(dataSource, "DataSource must not be null.");

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);

        if (incrementerFactory == null) {
            incrementerFactory = new DefaultDataFieldMaxValueIncrementerFactory(dataSource);
        }

        if (databaseType == null) {
            databaseType = DatabaseType.fromMetaData(dataSource).name();
            logger.info("No database type set, using meta data indicating: " + databaseType);
        }

        if (lobHandler == null && databaseType.equalsIgnoreCase(DatabaseType.ORACLE.toString())) {
            lobHandler = new OracleLobHandler();
        }

        Assert.isTrue(incrementerFactory.isSupportedIncrementerType(databaseType), "'" + databaseType
                + "' is an unsupported database type.  The supported database types are "
                + StringUtils.arrayToCommaDelimitedString(incrementerFactory.getSupportedIncrementerTypes()));

        super.afterPropertiesSet();

    }

    @Override
    protected JobInstanceDao createJobInstanceDao() throws Exception {
        JdbcJobInstanceDao dao = new JdbcJobInstanceDao();
        dao.setJdbcTemplate(jdbcTemplate);
        dao.setJobIncrementer(incrementerFactory.getIncrementer(databaseType, tablePrefix + "JOB_SEQ"));
        dao.setTablePrefix(tablePrefix);
        dao.afterPropertiesSet();
        return dao;
    }

    @Override
    protected JobExecutionDao createJobExecutionDao() throws Exception {
        JdbcJobExecutionDao dao = new JdbcJobExecutionDao();
        dao.setJdbcTemplate(jdbcTemplate);
        dao.setJobExecutionIncrementer(incrementerFactory.getIncrementer(databaseType, tablePrefix
                + "JOB_EXECUTION_SEQ"));
        dao.setTablePrefix(tablePrefix);
        dao.setClobTypeToUse(determineClobTypeToUse(this.databaseType));
        dao.setExitMessageLength(maxVarCharLength);
        dao.afterPropertiesSet();
        return dao;
    }

    @Override
    protected StepExecutionDao createStepExecutionDao() throws Exception {
        JdbcStepExecutionDao dao = new JdbcStepExecutionDao();
        dao.setJdbcTemplate(jdbcTemplate);
        dao.setStepExecutionIncrementer(incrementerFactory.getIncrementer(databaseType, tablePrefix
                + "STEP_EXECUTION_SEQ"));
        dao.setTablePrefix(tablePrefix);
        dao.setClobTypeToUse(determineClobTypeToUse(this.databaseType));
        dao.setExitMessageLength(maxVarCharLength);
        dao.afterPropertiesSet();
        return dao;
    }

    @Override
    protected ExecutionContextDao createExecutionContextDao() throws Exception {
    	JdbcExecutionContextDaoForTempData dao = new  JdbcExecutionContextDaoForTempData();
        dao.setJdbcTemplate(jdbcTemplate);
        dao.setTablePrefix(tablePrefix);
        dao.setClobTypeToUse(determineClobTypeToUse(this.databaseType));
        if (lobHandler != null) {
            dao.setLobHandler(lobHandler);
        }
        dao.afterPropertiesSet();
        // Assume the same length.
        dao.setShortContextLength(maxVarCharLength);
        return dao;
    }

    private int determineClobTypeToUse(String databaseType) {
        if (SYBASE == DatabaseType.valueOf(databaseType.toUpperCase())) {
            return Types.LONGVARCHAR;
        }
        else {
            return Types.CLOB;
        }
    }

    
}
