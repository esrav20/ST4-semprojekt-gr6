package dk.sdu.CommonInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Spring interface der giver os CRUD operations

public interface InventoryRepos extends JpaRepository<InventoryItems, Long> {
    Optional<InventoryItems> findByTrayId(int trayId);
}
