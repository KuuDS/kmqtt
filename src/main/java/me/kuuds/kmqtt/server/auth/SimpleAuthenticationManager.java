package me.kuuds.kmqtt.server.auth;

public class SimpleAuthenticationManager implements AuthenticationManager {

    @Override
    public boolean auth() {
        return true;
    }


}
