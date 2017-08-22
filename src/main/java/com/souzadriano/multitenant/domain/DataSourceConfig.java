package com.souzadriano.multitenant.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DataSourceConfig.
 */
@Entity
@Table(name = "mt_datasource_config")
public class DataSourceConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "jhi_password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "initialize", nullable = false)
    private Boolean initialize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public DataSourceConfig name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public DataSourceConfig url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public DataSourceConfig username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public DataSourceConfig password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isInitialize() {
        return initialize;
    }

    public DataSourceConfig initialize(Boolean initialize) {
        this.initialize = initialize;
        return this;
    }

    public void setInitialize(Boolean initialize) {
        this.initialize = initialize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataSourceConfig dataSourceConfig = (DataSourceConfig) o;
        if (dataSourceConfig.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), dataSourceConfig.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DataSourceConfig{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", initialize='" + isInitialize() + "'" +
            "}";
    }
}
