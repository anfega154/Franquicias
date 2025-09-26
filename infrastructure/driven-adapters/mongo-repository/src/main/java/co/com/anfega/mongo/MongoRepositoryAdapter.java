package co.com.anfega.mongo;

import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import co.com.anfega.mongo.entity.FranchiseEntity;
import co.com.anfega.mongo.helper.AdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class MongoRepositoryAdapter extends AdapterOperations<Franchise, FranchiseEntity, String, MongoDBRepository>
        implements FranchiseRepository {

    public MongoRepositoryAdapter(MongoDBRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Franchise.class/* change for domain model */));
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity data = new FranchiseEntity();
        data.setName(franchise.getName());

        return repository.save(data)
                .map(savedData -> {
                    Franchise result = new Franchise();
                    result.setId(savedData.getId());
                    result.setName(savedData.getName());
                    return result;
                })
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.error(new RuntimeException("Error al guardar la franquicia", e));
                });
    }
}
