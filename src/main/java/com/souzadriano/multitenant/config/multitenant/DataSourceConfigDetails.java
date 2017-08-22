package com.souzadriano.multitenant.config.multitenant;


import java.io.Serializable;

/**
 * A DataSourceConfig.
 */
public class DataSourceConfigDetails implements Serializable {


	private static final long serialVersionUID = -3182415708461433761L;
	
	private String name;
    private String url;
    private String username;
    private String password;
    
    public DataSourceConfigDetails() {
		super();
	}
    
	public DataSourceConfigDetails(String name, String url, String username, String password) {
		super();
		this.name = name;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getName() {
        return name;
    }

    public DataSourceConfigDetails name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public DataSourceConfigDetails url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public DataSourceConfigDetails username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public DataSourceConfigDetails password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DataSourceConfig{" +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
