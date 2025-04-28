package dk.sdu.Common;

import dk.sdu.IAssemblyStation;
import dk.sdu.IMqttConnection;

public class AssemblyStation implements IAssemblyStation {
    protected final IMqttConnection connection;

    public AssemblyStation(IMqttConnection connection) {
        this.connection = connection;
    }
    @Override
    public void start() throws Exception {
        connection.connect();
        setupSubscription();

    }

    @Override
    public void stop() throws Exception {
        connection.disconnect();

    }
    protected void setupSubscription() throws Exception{}
}
