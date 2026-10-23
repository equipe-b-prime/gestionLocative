package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.BailFinancierResponseDto;
import com.gestionLocative.gestionLocative.entity.BailFinancierView;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.repository.BailFinancierRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier des statistiques financières d'un bail.
 * Il est le seul à connaître la conversion de la vue v_bail_financier.
 * La lecture de la vue est portée par {@link BailFinancierRepository}, exactement
 * comme la lecture de v_client_stats est portée par ClientRepository en billetterie.
 */
@Service
public class BailStatsService {

    private final BailFinancierRepository bailFinancierRepository;

    /**
     * Constructeur injectant le repository des baux, qui porte la lecture de la vue.
     *
     * @param bailFinancierRepository le repository de la vue v_bail_financier
     */
    public BailStatsService(BailFinancierRepository bailFinancierRepository) {
        this.bailFinancierRepository = bailFinancierRepository;
    }

    /**
     * Convertit une ligne de la vue en objet de réponse.
     *
     * @param vue la ligne agrégée issue de la vue v_bail_financier
     * @return BailFinancierResponseDto la situation financière du bail
     */
    public BailFinancierResponseDto toDto(BailFinancierView vue) {
        return BailFinancierResponseDto.builder()
                .bailId(vue.getBailId())
                .nbQuittances(vue.getNbQuittances())
                .totalFacture(vue.getTotalFacture())
                .totalEncaisse(vue.getTotalEncaisse())
                .resteDu(vue.getResteDu())
                .nbQuittancesNonPayees(vue.getNbQuittancesNonPayees())
                .dernierMoisFacture(vue.getDernierMoisFacture())
                .dernierPaiement(vue.getDernierPaiement())
                .build();
    }

    /**
     * Récupère la situation financière agrégée d'un bail.
     * Un bail sans quittance obtient des compteurs et des montants à zéro ;
     * un bail inexistant n'apparaît pas dans la vue et provoque une erreur 404.
     *
     * @param id l'identifiant du bail
     * @return BailFinancierResponseDto la situation financière du bail
     * @throws NotFoundException si aucun bail ne porte cet identifiant
     */
    public BailFinancierResponseDto getBailStats(long id) {
        BailFinancierView vue = bailFinancierRepository.findByBailId(id)
                .orElseThrow(() -> new NotFoundException(CrudService.NOT_FOUND_MESSAGE));
        return toDto(vue);
    }
}
