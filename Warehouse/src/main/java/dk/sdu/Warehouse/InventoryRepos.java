package dk.sdu.Warehouse;

import dk.sdu.CommonInventory.InventoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//Spring interface der giver os CRUD operations
@Repository
public interface InventoryRepos extends JpaRepository<InventoryItems, Long> {
    List<InventoryView> findAllBy();
    Optional<InventoryItems> findByTrayId(int trayId); // full entity for update/delete
}