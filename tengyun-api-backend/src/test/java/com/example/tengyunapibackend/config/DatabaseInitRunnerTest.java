package com.example.tengyunapibackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseInitRunnerTest {

    @Test
    void shouldCreateUniqueIndexWhenMissing() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), any(Class.class), any(), any())).thenReturn(0);

        DatabaseInitRunner runner = new DatabaseInitRunner();
        ReflectionTestUtils.setField(runner, "jdbcTemplate", jdbcTemplate);

        runner.run();

        verify(jdbcTemplate, times(1))
                .execute("CREATE UNIQUE INDEX uk_user_interface ON user_interface_info (user_id, interface_info_id)");
    }

    @Test
    void shouldSkipUniqueIndexWhenExists() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), any(Class.class), any(), any())).thenReturn(1);

        DatabaseInitRunner runner = new DatabaseInitRunner();
        ReflectionTestUtils.setField(runner, "jdbcTemplate", jdbcTemplate);

        runner.run();

        verify(jdbcTemplate, times(0))
                .execute("CREATE UNIQUE INDEX uk_user_interface ON user_interface_info (user_id, interface_info_id)");
    }
}
