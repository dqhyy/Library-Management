package com.huy.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration 
@EnableJpaAuditing // Bật tính năng tự động ghi nhận thông tin về thời điểm tạo cập nhật
public class JpaAuditingConfig {
    
}
