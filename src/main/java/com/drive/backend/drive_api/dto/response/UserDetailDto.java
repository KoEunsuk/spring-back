package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.enums.Grade;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.entity.User;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class UserDetailDto {
    // 공통 필드
    private final Long userId;
    private final String email;
    private final String username;
    private final String phoneNumber;
    private final String imagePath;
    private final Role role;
    private final Long operatorId;
    private final String operatorName;

    // 역할별 확장 데이터
    private final Map<String, Object> payload;

    private UserDetailDto(User user, Map<String, Object> payload) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.imagePath = user.getImagePath();
        this.role = user.getRole();
        Operator operator = user.getOperator();
        this.operatorId = (operator != null) ? operator.getOperatorId() : null;
        this.operatorName = (operator != null) ? operator.getOperatorName() : null;

        this.payload = payload;
    }

    public static UserDetailDto from(User user) {
        Map<String, Object> payload = new HashMap<>();

        if (user instanceof Driver driver) {
            payload.put("licenseNumber", driver.getLicenseNumber());
            payload.put("careerYears", driver.getCareerYears());
            payload.put("grade", driver.getGrade());
            payload.put("avgDrowsinessCount", driver.getAvgDrowsinessCount());
            payload.put("avgAccelerationCount", driver.getAvgAccelerationCount());
            payload.put("avgBrakingCount", driver.getAvgBrakingCount());
            payload.put("avgAbnormalCount", driver.getAvgAbnormalCount());
            payload.put("avgDrivingScore", driver.getAvgDrivingScore());
        } else if (user instanceof Admin) {
            // Admin은 별도 payload 없음 → 빈 Map 유지
        }

        return new UserDetailDto(user, payload.isEmpty() ? null : payload);
    }
}
