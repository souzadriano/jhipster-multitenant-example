package com.souzadriano.multitenant.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.souzadriano.multitenant.domain.DataSourceConfig;

import com.souzadriano.multitenant.repository.DataSourceConfigRepository;
import com.souzadriano.multitenant.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing DataSourceConfig.
 */
@RestController
@RequestMapping("/api")
public class DataSourceConfigResource {

    private final Logger log = LoggerFactory.getLogger(DataSourceConfigResource.class);

    private static final String ENTITY_NAME = "dataSourceConfig";

    private final DataSourceConfigRepository dataSourceConfigRepository;

    public DataSourceConfigResource(DataSourceConfigRepository dataSourceConfigRepository) {
        this.dataSourceConfigRepository = dataSourceConfigRepository;
    }

    /**
     * POST  /data-source-configs : Create a new dataSourceConfig.
     *
     * @param dataSourceConfig the dataSourceConfig to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dataSourceConfig, or with status 400 (Bad Request) if the dataSourceConfig has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/data-source-configs")
    @Timed
    public ResponseEntity<DataSourceConfig> createDataSourceConfig(@Valid @RequestBody DataSourceConfig dataSourceConfig) throws URISyntaxException {
        log.debug("REST request to save DataSourceConfig : {}", dataSourceConfig);
        if (dataSourceConfig.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dataSourceConfig cannot already have an ID")).body(null);
        }
        DataSourceConfig result = dataSourceConfigRepository.save(dataSourceConfig);
        return ResponseEntity.created(new URI("/api/data-source-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /data-source-configs : Updates an existing dataSourceConfig.
     *
     * @param dataSourceConfig the dataSourceConfig to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dataSourceConfig,
     * or with status 400 (Bad Request) if the dataSourceConfig is not valid,
     * or with status 500 (Internal Server Error) if the dataSourceConfig couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/data-source-configs")
    @Timed
    public ResponseEntity<DataSourceConfig> updateDataSourceConfig(@Valid @RequestBody DataSourceConfig dataSourceConfig) throws URISyntaxException {
        log.debug("REST request to update DataSourceConfig : {}", dataSourceConfig);
        if (dataSourceConfig.getId() == null) {
            return createDataSourceConfig(dataSourceConfig);
        }
        DataSourceConfig result = dataSourceConfigRepository.save(dataSourceConfig);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dataSourceConfig.getId().toString()))
            .body(result);
    }

    /**
     * GET  /data-source-configs : get all the dataSourceConfigs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of dataSourceConfigs in body
     */
    @GetMapping("/data-source-configs")
    @Timed
    public List<DataSourceConfig> getAllDataSourceConfigs() {
        log.debug("REST request to get all DataSourceConfigs");
        return dataSourceConfigRepository.findAll();
    }

    /**
     * GET  /data-source-configs/:id : get the "id" dataSourceConfig.
     *
     * @param id the id of the dataSourceConfig to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dataSourceConfig, or with status 404 (Not Found)
     */
    @GetMapping("/data-source-configs/{id}")
    @Timed
    public ResponseEntity<DataSourceConfig> getDataSourceConfig(@PathVariable Long id) {
        log.debug("REST request to get DataSourceConfig : {}", id);
        DataSourceConfig dataSourceConfig = dataSourceConfigRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dataSourceConfig));
    }

    /**
     * DELETE  /data-source-configs/:id : delete the "id" dataSourceConfig.
     *
     * @param id the id of the dataSourceConfig to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/data-source-configs/{id}")
    @Timed
    public ResponseEntity<Void> deleteDataSourceConfig(@PathVariable Long id) {
        log.debug("REST request to delete DataSourceConfig : {}", id);
        dataSourceConfigRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
