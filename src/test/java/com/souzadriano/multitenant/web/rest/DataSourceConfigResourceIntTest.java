package com.souzadriano.multitenant.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.souzadriano.multitenant.MultitenantApp;
import com.souzadriano.multitenant.domain.DataSourceConfig;
import com.souzadriano.multitenant.repository.DataSourceConfigRepository;
import com.souzadriano.multitenant.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the DataSourceConfigResource REST controller.
 *
 * @see DataSourceConfigResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MultitenantApp.class)
public class DataSourceConfigResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_DRIVER_CLASS_NAME = "AAAAAAAAAA";

    private static final Boolean DEFAULT_INITIALIZE = false;
    private static final Boolean UPDATED_INITIALIZE = true;

    @Autowired
    private DataSourceConfigRepository dataSourceConfigRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDataSourceConfigMockMvc;

    private DataSourceConfig dataSourceConfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DataSourceConfigResource dataSourceConfigResource = new DataSourceConfigResource(dataSourceConfigRepository);
        this.restDataSourceConfigMockMvc = MockMvcBuilders.standaloneSetup(dataSourceConfigResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataSourceConfig createEntity(EntityManager em) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig()
            .name(DEFAULT_NAME)
            .url(DEFAULT_URL)
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD)
            .initialize(DEFAULT_INITIALIZE);
        return dataSourceConfig;
    }

    @Before
    public void initTest() {
        dataSourceConfig = createEntity(em);
    }

    @Test
    @Transactional
    public void createDataSourceConfig() throws Exception {
        int databaseSizeBeforeCreate = dataSourceConfigRepository.findAll().size();

        // Create the DataSourceConfig
        restDataSourceConfigMockMvc.perform(post("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isCreated());

        // Validate the DataSourceConfig in the database
        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeCreate + 1);
        DataSourceConfig testDataSourceConfig = dataSourceConfigList.get(dataSourceConfigList.size() - 1);
        assertThat(testDataSourceConfig.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDataSourceConfig.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testDataSourceConfig.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testDataSourceConfig.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testDataSourceConfig.isInitialize()).isEqualTo(DEFAULT_INITIALIZE);
    }

    @Test
    @Transactional
    public void createDataSourceConfigWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dataSourceConfigRepository.findAll().size();

        // Create the DataSourceConfig with an existing ID
        dataSourceConfig.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDataSourceConfigMockMvc.perform(post("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataSourceConfigRepository.findAll().size();
        // set the field null
        dataSourceConfig.setName(null);

        // Create the DataSourceConfig, which fails.

        restDataSourceConfigMockMvc.perform(post("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isBadRequest());

        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataSourceConfigRepository.findAll().size();
        // set the field null
        dataSourceConfig.setUrl(null);

        // Create the DataSourceConfig, which fails.

        restDataSourceConfigMockMvc.perform(post("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isBadRequest());

        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataSourceConfigRepository.findAll().size();
        // set the field null
        dataSourceConfig.setUsername(null);

        // Create the DataSourceConfig, which fails.

        restDataSourceConfigMockMvc.perform(post("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isBadRequest());

        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataSourceConfigRepository.findAll().size();
        // set the field null
        dataSourceConfig.setPassword(null);

        // Create the DataSourceConfig, which fails.

        restDataSourceConfigMockMvc.perform(post("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isBadRequest());

        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkInitializeIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataSourceConfigRepository.findAll().size();
        // set the field null
        dataSourceConfig.setInitialize(null);

        // Create the DataSourceConfig, which fails.

        restDataSourceConfigMockMvc.perform(post("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isBadRequest());

        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDataSourceConfigs() throws Exception {
        // Initialize the database
        dataSourceConfigRepository.saveAndFlush(dataSourceConfig);

        // Get all the dataSourceConfigList
        restDataSourceConfigMockMvc.perform(get("/api/data-source-configs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataSourceConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
            .andExpect(jsonPath("$.[*].driverClassName").value(hasItem(DEFAULT_DRIVER_CLASS_NAME.toString())))
            .andExpect(jsonPath("$.[*].initialize").value(hasItem(DEFAULT_INITIALIZE.booleanValue())));
    }

    @Test
    @Transactional
    public void getDataSourceConfig() throws Exception {
        // Initialize the database
        dataSourceConfigRepository.saveAndFlush(dataSourceConfig);

        // Get the dataSourceConfig
        restDataSourceConfigMockMvc.perform(get("/api/data-source-configs/{id}", dataSourceConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dataSourceConfig.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
            .andExpect(jsonPath("$.driverClassName").value(DEFAULT_DRIVER_CLASS_NAME.toString()))
            .andExpect(jsonPath("$.initialize").value(DEFAULT_INITIALIZE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDataSourceConfig() throws Exception {
        // Get the dataSourceConfig
        restDataSourceConfigMockMvc.perform(get("/api/data-source-configs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDataSourceConfig() throws Exception {
        // Initialize the database
        dataSourceConfigRepository.saveAndFlush(dataSourceConfig);
        int databaseSizeBeforeUpdate = dataSourceConfigRepository.findAll().size();

        // Update the dataSourceConfig
        DataSourceConfig updatedDataSourceConfig = dataSourceConfigRepository.findOne(dataSourceConfig.getId());
        updatedDataSourceConfig
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .initialize(UPDATED_INITIALIZE);

        restDataSourceConfigMockMvc.perform(put("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDataSourceConfig)))
            .andExpect(status().isOk());

        // Validate the DataSourceConfig in the database
        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeUpdate);
        DataSourceConfig testDataSourceConfig = dataSourceConfigList.get(dataSourceConfigList.size() - 1);
        assertThat(testDataSourceConfig.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDataSourceConfig.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testDataSourceConfig.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testDataSourceConfig.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testDataSourceConfig.isInitialize()).isEqualTo(UPDATED_INITIALIZE);
    }

    @Test
    @Transactional
    public void updateNonExistingDataSourceConfig() throws Exception {
        int databaseSizeBeforeUpdate = dataSourceConfigRepository.findAll().size();

        // Create the DataSourceConfig

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDataSourceConfigMockMvc.perform(put("/api/data-source-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSourceConfig)))
            .andExpect(status().isCreated());

        // Validate the DataSourceConfig in the database
        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDataSourceConfig() throws Exception {
        // Initialize the database
        dataSourceConfigRepository.saveAndFlush(dataSourceConfig);
        int databaseSizeBeforeDelete = dataSourceConfigRepository.findAll().size();

        // Get the dataSourceConfig
        restDataSourceConfigMockMvc.perform(delete("/api/data-source-configs/{id}", dataSourceConfig.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigRepository.findAll();
        assertThat(dataSourceConfigList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataSourceConfig.class);
        DataSourceConfig dataSourceConfig1 = new DataSourceConfig();
        dataSourceConfig1.setId(1L);
        DataSourceConfig dataSourceConfig2 = new DataSourceConfig();
        dataSourceConfig2.setId(dataSourceConfig1.getId());
        assertThat(dataSourceConfig1).isEqualTo(dataSourceConfig2);
        dataSourceConfig2.setId(2L);
        assertThat(dataSourceConfig1).isNotEqualTo(dataSourceConfig2);
        dataSourceConfig1.setId(null);
        assertThat(dataSourceConfig1).isNotEqualTo(dataSourceConfig2);
    }
}
