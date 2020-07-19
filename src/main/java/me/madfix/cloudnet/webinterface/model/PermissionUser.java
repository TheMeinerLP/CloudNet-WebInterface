package me.madfix.cloudnet.webinterface.model;

import me.madfix.cloudnet.webinterface.api.permission.Permissible;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PermissionUser extends WebInterfaceUser implements Permissible {

    private final Collection<String> permissions;

    public PermissionUser(int id, String username, byte[] passwordHash, Collection<String> permissions) {
        super(id, username, passwordHash);
        this.permissions = permissions;
    }

    @Override
    public boolean hasPermission(String permission) {
        boolean has = false;
        if (isNumberPermission(permission)) {
            List<String> numberPermissions = permissions.stream().filter(this::isNumberPermission).collect(Collectors.toList());
            String numberPermissionPrefix = numberPermissionPrefix(permission);
            List<String> numberPermissionsPrefix = numberPermissions.stream().map(this::numberPermissionPrefix).collect(Collectors.toList());
            if (numberPermissionsPrefix.contains(numberPermissionPrefix)) {
                int permissionValue = getPermissionValue(permission);
                Optional<String> optional = permissions.stream().filter(this::isNumberPermission).filter(s -> s.startsWith(numberPermissionPrefix)).findFirst();
                if (optional.isPresent()) {
                    int permissionValueList = getPermissionValue(optional.get());
                    if (permissionValue >= permissionValueList) {
                        has = true;
                    }
                }
            }
        } else {
            if (permissions.contains(permission)) {
                has = true;
            }
        }
        return has;
    }

    private String numberPermissionPrefix(String permission) {
        return permission.substring(0, permission.lastIndexOf(".") - 1);
    }

    private boolean isNumberPermission(String permission) {
        String[] split = permission.split("\\.");
        String endPermission = split[split.length - 1];
        return endPermission.matches("[0-9]");
    }

    private int getPermissionValue(String permission) {
        String[] split = permission.split("\\.");
        String endPermission = split[split.length - 1];
        return Integer.parseInt(endPermission);
    }

}
