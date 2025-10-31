package varta.service;

import varta.model.pgsql.IngestionProgress;
import varta.repository.pgsql.IngestionProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class IngestionProgressService {

    final
    IngestionProgressRepository ingestionProgressRepository;

    public IngestionProgressService(IngestionProgressRepository ingestionProgressRepository) {
        this.ingestionProgressRepository = ingestionProgressRepository;
    }

    public void updateIngestionProgress(String datasetName, Integer batchSize) {
        Optional<IngestionProgress> ingestionProgressOptional = ingestionProgressRepository.findById(datasetName);
        IngestionProgress ingestionProgressObj;

        if (ingestionProgressOptional.isPresent()) {
            log.info("Updating ingestion progress for '{}' dataset", datasetName);
          ingestionProgressObj = ingestionProgressOptional.get();
          Integer lastProcessedId = ingestionProgressObj.getLastProcessedId();
          ingestionProgressObj.setLastProcessedId(lastProcessedId + batchSize);
          log.info("Last updated id for '{}' dataset was updated: {} -> {}", datasetName, lastProcessedId, lastProcessedId+batchSize);
        }
        else {
            log.warn("Creating ingestion progress record for '{}' dataset, last updated is is {}", datasetName, batchSize);
            ingestionProgressRepository.save(
                  IngestionProgress.builder().datasetName(datasetName).lastProcessedId(batchSize)
                          .build());
        }
    }

}
