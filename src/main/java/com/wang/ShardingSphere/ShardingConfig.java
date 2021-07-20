package com.wang.ShardingSphere;

import com.google.common.collect.Range;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
public class ShardingConfig {

    @ConfigurationProperties(prefix = "spring.shardingsphere.datasource.ds0")
    @Bean(name = "ds0")
    public DataSource dataSource0() {
        return new HikariDataSource();
    }

    @ConfigurationProperties(prefix = "spring.shardingsphere.datasource.ds1")
    @Bean(name = "ds1")
    public DataSource dataSource1() {
        return new HikariDataSource();
    }

    @ConfigurationProperties(prefix = "spring.shardingsphere.datasource.ds2")
    @Bean(name = "ds2")
    public DataSource dataSource2() {
        return new HikariDataSource();
    }

    @Value("${spring.shardingsphere.sharding.tables.user.actualDataNodes}")
    private String ordersActualDataNodes;

    private String ordersLogicTable = "user";

    @Value("${spring.shardingsphere.sharding.tables.user.databaseStrategy.inline.shardingColumn}")
    private String databaseShardingColumn;
    @Value("${spring.shardingsphere.sharding.tables.user.tableStrategy.inline.shardingColumn}")
    private String userShardingColumn;
    private String defaultDataSource = "ds0";


    @Primary
    @Bean(name = "shardingDataSource")
    public DataSource getDataSource(@Qualifier("ds0") DataSource ds_0, @Qualifier("ds1") DataSource ds_1, @Qualifier("ds2") DataSource ds_2) throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getUserTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add(ordersLogicTable);
        // 分库
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", new DatabaseShardingAlgorithm()));
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", new TablePreciseShardingAlgorithm(), new TableRangeShardingAlgorithm()));
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ds0", ds_0);
        dataSourceMap.put("ds1", ds_1);
        dataSourceMap.put("ds2", ds_2);
        //设置默认数据库
        shardingRuleConfig.setDefaultDataSourceName(defaultDataSource);
        Properties properties = new Properties();
        properties.setProperty("sql.show", Boolean.TRUE.toString());
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, properties);
        return dataSource;
    }

    private TableRuleConfiguration getUserTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration(ordersLogicTable, ordersActualDataNodes);
        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration(databaseShardingColumn, new DatabaseShardingAlgorithm()));
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(userShardingColumn, new TablePreciseShardingAlgorithm()));
        return orderTableRuleConfig;
    }

}

/**
 * 分库算法
 */
final class DatabaseShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
        for (String each : availableTargetNames) {
            if (each.endsWith(shardingValue.getValue() % 2 + "")) {
                return each;
            }
        }
        return null;
    }
}

/**
 * 分表算法
 */
final class TablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {
    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<Integer> shardingValue) {
        for (String each : availableTargetNames) {
            if (each.endsWith(shardingValue.getValue() % 2 + "")) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }
}

final class TableRangeShardingAlgorithm implements RangeShardingAlgorithm<Integer> {
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Integer> rangeShardingValue) {
        Collection<String> collect = new ArrayList<>();
        Range<Integer> valueRange = rangeShardingValue.getValueRange();
        for (Integer i = valueRange.lowerEndpoint(); i <= valueRange.upperEndpoint(); i++) {
            for (String each : collection) {
                if (each.endsWith(i % 2 + "")) {
                    collect.add(each);
                }
            }
        }
        return collect;
    }

}
