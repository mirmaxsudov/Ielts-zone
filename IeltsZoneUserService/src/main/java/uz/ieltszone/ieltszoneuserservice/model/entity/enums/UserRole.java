package uz.ieltszone.ieltszoneuserservice.model.entity.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    SUPER_ADMIN,
    ADMIN,
    STUDENT,
    TEACHER,
    MANAGER,
    PARENT;
}