package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Date;

public class BailRequestDto implements RequestDto {
    private Date dateDebut;
    private Date dateFin;
    private BigDecimal montantLoyer;
    private BigDecimal provisionCharges;
    private BigDecimal depotGarantie;
    private Long appartementId;
    private Long locataireId;

    public BailRequestDto() {
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public BigDecimal getMontantLoyer() {
        return montantLoyer;
    }

    public void setMontantLoyer(BigDecimal montantLoyer) {
        this.montantLoyer = montantLoyer;
    }

    public BigDecimal getProvisionCharges() {
        return provisionCharges;
    }

    public void setProvisionCharges(BigDecimal provisionCharges) {
        this.provisionCharges = provisionCharges;
    }

    public BigDecimal getDepotGarantie() {
        return depotGarantie;
    }

    public void setDepotGarantie(BigDecimal depotGarantie) {
        this.depotGarantie = depotGarantie;
    }

    public Long getAppartementId() {
        return appartementId;
    }

    public void setAppartementId(Long appartementId) {
        this.appartementId = appartementId;
    }

    public Long getLocataireId() {
        return locataireId;
    }

    public void setLocataireId(Long locataireId) {
        this.locataireId = locataireId;
    }
}
