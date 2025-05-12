package dk.sdu.CommonInventory.Service;

import dk.sdu.CommonInventory.InventoryItems;
import dk.sdu.CommonInventory.InventoryRepos;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@ConfigurationProperties("service")
//connector vores repository til resten af applikationen
@Service
public class soapWarehouseService implements InventoryRepos {
    private final InventoryRepos inventoryRepos;
    //private final IEmulatorService servicePort;
    @Autowired
    public soapWarehouseService(InventoryRepos inventoryRepos) {
        this.inventoryRepos = inventoryRepos;
        //IEmulatorService_Service service = new IEmulatorService_Service();
        //this.servicePort = service.getBasicHttpBindingIEmulatorService();
    }


    //returnere inventory
    public List<InventoryItems> getInventory(){
        return inventoryRepos.findAll();
    }

    //Handler at kunne indsætte items på trays
    public InventoryItems insertItem(int trayId, String itemName){
        InventoryItems item = new InventoryItems();
        item.setTrayId(trayId);
        item.setItemName(itemName);
        item.setQuantity(1);
        return inventoryRepos.save(item);
    }


    //Kan finde/fjerne inventory i bestemt tray
    public String pickItem(int trayId) {
        InventoryItems item = inventoryRepos.findByTrayId(trayId)
                .orElseThrow(()-> new RuntimeException("Item not found"));
        item.setQuantity(item.getQuantity()-1);
        if(item.getQuantity()<= 0){
            inventoryRepos.delete(item);
            return "Item picked and tray now empty";
        } else{
            inventoryRepos.save(item);
            return "Item picked, remaining quantity: "+ item.getQuantity();
        }
    }

    @Override
    public Optional<InventoryItems> findByTrayId(int trayId) {
        return Optional.empty();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends InventoryItems> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends InventoryItems> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<InventoryItems> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public InventoryItems getOne(Long aLong) {
        return null;
    }

    @Override
    public InventoryItems getById(Long aLong) {
        return null;
    }

    @Override
    public InventoryItems getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends InventoryItems> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends InventoryItems> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends InventoryItems> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends InventoryItems> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends InventoryItems> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends InventoryItems> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends InventoryItems, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends InventoryItems> S save(S entity) {
        return null;
    }

    @Override
    public <S extends InventoryItems> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<InventoryItems> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<InventoryItems> findAll() {
        return null;
    }

    @Override
    public List<InventoryItems> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(InventoryItems entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends InventoryItems> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<InventoryItems> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<InventoryItems> findAll(Pageable pageable) {
        return null;
    }
}
