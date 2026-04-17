package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tengyunapibackend.model.vo.TextRepairReport;
import com.example.tengyunapibackend.service.InterfaceInfoService;
import com.example.tengyunapibackend.service.TextDataRepairService;
import com.example.tengyunapibackend.service.UserService;
import com.example.tengyunapibackend.utils.TextFixUtils;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.example.tengyunapicommon.entity.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Service
public class TextDataRepairServiceImpl implements TextDataRepairService {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TextRepairReport repairMojibake(boolean dryRun) {
        TextRepairReport report = new TextRepairReport();
        report.setDryRun(dryRun);

        repairUsers(dryRun, report);
        repairInterfaces(dryRun, report);

        int totalUpdated = report.getUpdatedUserNameCount()
                + report.getUpdatedInterfaceNameCount()
                + report.getUpdatedInterfaceDescriptionCount();
        report.setTotalUpdatedCount(totalUpdated);
        return report;
    }

    private void repairUsers(boolean dryRun, TextRepairReport report) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        List<User> users = userService.list(queryWrapper);
        report.setScannedUserCount(users.size());
        for (User user : users) {
            String origin = user.getUserName();
            String fixed = TextFixUtils.fixMojibake(origin);
            boolean placeholder = isQuestionPlaceholder(fixed);
            if (placeholder) {
                report.setPlaceholderUserNameCount(report.getPlaceholderUserNameCount() + 1);
                fixed = buildUserNameFallback(user);
            }
            if (same(origin, fixed)) {
                continue;
            }
            if (!dryRun) {
                User updateUser = new User();
                updateUser.setId(user.getId());
                updateUser.setUserName(fixed);
                userService.updateById(updateUser);
            }
            report.setUpdatedUserNameCount(report.getUpdatedUserNameCount() + 1);
            addSample(report, "user.user_name", user.getId(), origin, fixed);
        }
    }

    private void repairInterfaces(boolean dryRun, TextRepairReport report) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        List<InterfaceInfo> interfaces = interfaceInfoService.list(queryWrapper);
        report.setScannedInterfaceCount(interfaces.size());
        for (InterfaceInfo interfaceInfo : interfaces) {
            String originalName = interfaceInfo.getName();
            String fixedName = TextFixUtils.fixMojibake(originalName);
            boolean namePlaceholder = isQuestionPlaceholder(fixedName);
            if (namePlaceholder) {
                report.setPlaceholderInterfaceNameCount(report.getPlaceholderInterfaceNameCount() + 1);
                fixedName = buildInterfaceNameFallback(interfaceInfo);
            }

            String originalDesc = interfaceInfo.getDescription();
            String fixedDesc = TextFixUtils.fixMojibake(originalDesc);
            boolean descPlaceholder = isQuestionPlaceholder(fixedDesc);
            if (descPlaceholder) {
                report.setPlaceholderInterfaceDescriptionCount(report.getPlaceholderInterfaceDescriptionCount() + 1);
                fixedDesc = buildInterfaceDescriptionFallback(interfaceInfo);
            }

            boolean nameChanged = !same(originalName, fixedName);
            boolean descChanged = !same(originalDesc, fixedDesc);
            if (!nameChanged && !descChanged) {
                continue;
            }
            if (!dryRun) {
                InterfaceInfo updateInterface = new InterfaceInfo();
                updateInterface.setId(interfaceInfo.getId());
                if (nameChanged) {
                    updateInterface.setName(fixedName);
                }
                if (descChanged) {
                    updateInterface.setDescription(fixedDesc);
                }
                interfaceInfoService.updateById(updateInterface);
            }
            if (nameChanged) {
                report.setUpdatedInterfaceNameCount(report.getUpdatedInterfaceNameCount() + 1);
                addSample(report, "interface_info.name", interfaceInfo.getId(), originalName, fixedName);
            }
            if (descChanged) {
                report.setUpdatedInterfaceDescriptionCount(report.getUpdatedInterfaceDescriptionCount() + 1);
                addSample(report, "interface_info.description", interfaceInfo.getId(), originalDesc, fixedDesc);
            }
        }
    }

    private static boolean same(String left, String right) {
        return Objects.equals(left, right);
    }

    private static boolean isQuestionPlaceholder(String text) {
        if (text == null) {
            return false;
        }
        String value = text.trim();
        if (value.isEmpty()) {
            return false;
        }
        if (value.matches("^[?？]+$")) {
            return true;
        }
        if (!(value.contains("??") || value.contains("？？"))) {
            return false;
        }
        return !containsCjk(value);
    }

    private static boolean containsCjk(String text) {
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 0x4E00 && ch <= 0x9FFF) {
                return true;
            }
        }
        return false;
    }

    private static String buildUserNameFallback(User user) {
        String userAccount = user.getUserAccount();
        if (userAccount != null && !userAccount.isBlank()) {
            return userAccount.trim();
        }
        return "user-" + user.getId();
    }

    private static String buildInterfaceNameFallback(InterfaceInfo interfaceInfo) {
        String method = interfaceInfo.getMethod() == null || interfaceInfo.getMethod().isBlank()
                ? "API"
                : interfaceInfo.getMethod().trim().toUpperCase();
        String path = normalizePath(interfaceInfo.getUrl());
        return method + " " + path;
    }

    private static String buildInterfaceDescriptionFallback(InterfaceInfo interfaceInfo) {
        return "Auto repaired description for interface id=" + interfaceInfo.getId();
    }

    private static String normalizePath(String url) {
        if (url == null || url.isBlank()) {
            return "/unknown";
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
                URI uri = URI.create(url);
                if (uri.getPath() != null && !uri.getPath().isBlank()) {
                    return uri.getPath();
                }
                return "/unknown";
            } catch (Exception ignored) {
                return url;
            }
        }
        return url;
    }

    private static void addSample(TextRepairReport report, String field, Long id, String origin, String fixed) {
        if (report.getSamples().size() >= 20) {
            return;
        }
        report.getSamples().add(
                field + "#" + id
                        + " | from=[" + safe(origin) + "] to=[" + safe(fixed) + "]"
        );
    }

    private static String safe(String text) {
        if (text == null) {
            return "null";
        }
        String value = text.replaceAll("\\s+", " ").trim();
        if (value.length() > 80) {
            return value.substring(0, 80) + "...";
        }
        return value;
    }
}

