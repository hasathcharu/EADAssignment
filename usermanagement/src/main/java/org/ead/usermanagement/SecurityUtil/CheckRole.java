package org.ead.usermanagement.SecurityUtil;

public class CheckRole {
    private String cleanInput(String input){
        return input.replaceAll("[\\[\\]\\s]", "");
    }
    public boolean checkRole(String role, String userRoles){
        String[] roles = cleanInput(userRoles).split(",");
        for(String r: roles){
            if(r.equals(role)){
                return true;
            }
        }
        return false;
    }
}
