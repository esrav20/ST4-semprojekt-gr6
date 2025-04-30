import dk.sdu.AGVPI;
import dk.sdu.AGVConnection.AGVMovement;
module AGV {
    requires org.json;
    requires CommonAGV;
    exports dk.sdu.AGVConnection;
    provides AGVPI with AGVMovement;
}