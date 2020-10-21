package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


@Entity
@NamedQuery(name = "CityInfo.deleteAllRows", query = "DELETE from CityInfo")
public class CityInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String zipcode;
    private String city;
    @OneToMany(mappedBy = "cityInfo", cascade = CascadeType.PERSIST)
    List<Address> addresses;
    
    public CityInfo() {
    }

    public CityInfo(String zipcode, String city) {
        this.zipcode = zipcode;
        this.city = city;
        this.addresses = new ArrayList<>();
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void addAddress(Address address) {
        if (address != null) {
            this.addresses.add(address);
            address.setCityInfo(this);
        }
    }
    
        
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
