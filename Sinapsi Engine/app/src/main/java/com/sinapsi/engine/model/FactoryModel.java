package com.sinapsi.engine.model;

public interface FactoryModel {
    public UserInterface newUser(int id, String email, String passowrd);
    public DeviceInterface newDevice(int id, String name, String model, String type, UserInterface user, int version);
    public MacroInterface newMacro(String name, int id);
}
