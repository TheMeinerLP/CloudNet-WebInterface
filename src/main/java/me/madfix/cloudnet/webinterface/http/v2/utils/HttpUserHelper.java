package me.madfix.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnet.lib.user.User;

public final class HttpUserHelper {

    /**
     * Check have the user the permission.
     *
     * @param user        The user to check the permissions
     * @param permissions The permission list to check
     * @return Return true if the user have the permission
     */
    public static boolean hasPermission(User user, String... permissions) {
        for (String permission : permissions) {
            //if(permission.matches("^.+?.*([.0-9]\\1$)")){ } Check permission end with number
            if (user.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

}