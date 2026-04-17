package com.example.tengyunapibackend.config;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitRunner implements CommandLineRunner {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS interface_invoke_log (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    interface_info_id BIGINT NOT NULL,
                    request_path VARCHAR(512) NOT NULL,
                    request_method VARCHAR(16) NOT NULL,
                    request_params TEXT NULL,
                    response_status INT NULL,
                    response_message TEXT NULL,
                    status INT NOT NULL DEFAULT 0,
                    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    is_delete TINYINT NOT NULL DEFAULT 0
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS user_interface_quota_record (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    interface_info_id BIGINT NOT NULL,
                    change_num INT NOT NULL,
                    before_left_num INT NOT NULL DEFAULT 0,
                    after_left_num INT NOT NULL DEFAULT 0,
                    operate_type VARCHAR(32) NOT NULL,
                    description VARCHAR(512) NULL,
                    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    is_delete TINYINT NOT NULL DEFAULT 0
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS admin_operate_log (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    operator_user_id BIGINT NOT NULL,
                    action VARCHAR(64) NOT NULL,
                    target_type VARCHAR(64) NULL,
                    target_id BIGINT NULL,
                    request_path VARCHAR(512) NULL,
                    request_method VARCHAR(16) NULL,
                    request_ip VARCHAR(64) NULL,
                    detail TEXT NULL,
                    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    is_delete TINYINT NOT NULL DEFAULT 0,
                    INDEX idx_operator_time (operator_user_id, create_time),
                    INDEX idx_action_time (action, create_time),
                    INDEX idx_target (target_type, target_id)
                )
                """);
        ensureUniqueIndexExists(
                "user_interface_info",
                "uk_user_interface",
                "CREATE UNIQUE INDEX uk_user_interface ON user_interface_info (user_id, interface_info_id)"
        );
    }

    private void ensureUniqueIndexExists(String tableName, String indexName, String createIndexSql) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND index_name = ?
                """, Integer.class, tableName, indexName);
        if (count == null || count == 0) {
            jdbcTemplate.execute(createIndexSql);
        }
    }
}
