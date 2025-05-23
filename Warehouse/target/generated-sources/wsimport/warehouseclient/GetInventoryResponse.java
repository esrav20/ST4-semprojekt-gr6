
package warehouseclient;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="GetInventoryResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getInventoryResult"
})
@XmlRootElement(name = "GetInventoryResponse")
public class GetInventoryResponse {

    @XmlElement(name = "GetInventoryResult", required = true, nillable = true)
    protected String getInventoryResult;

    /**
     * Gets the value of the getInventoryResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetInventoryResult() {
        return getInventoryResult;
    }

    /**
     * Sets the value of the getInventoryResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetInventoryResult(String value) {
        this.getInventoryResult = value;
    }

}
