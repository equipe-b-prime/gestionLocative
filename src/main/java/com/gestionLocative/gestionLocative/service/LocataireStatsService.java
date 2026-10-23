package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.LocataireSituationResponseDto;
import com.gestionLocative.gestionLocative.entity.LocataireSituationView;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.repository.LocataireSituationRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier des statistiques financières agrégées d'un locataire.
 * Il est le seul à connaître la conversion de la vue v_locataire_situation.
 * La lecture de la vue est portée par {@link LocataireSituationRepository}, exactement
 * comme la lecture de v_client_stats est portée par ClientRepository en billetterie.
 */
@Service
public class LocataireStatsService {

    private final LocataireSituationRepository locataireSituationRepository;

    /**
     * Constructeur injectant le repository des locataires, qui porte la lecture de la vue.
     *
     * @param locataireSituationRepository le repository de la vue v_locataire_situation
     */
    public LocataireStatsService(LocataireSituationRepository locataireSituationRepository) {
        this.locataireSituationRepository = locataireSituationRepository;
    }

    /**
     * Convertit une ligne de la vue en objet de réponse.
     *
     * @param vue la ligne agrégée issue de la vue v_locataire_situation
     * @return LocataireSituationResponseDto la situation financière du locataire
     */
    public LocataireSituationResponseDto toDto(LocataireSituationView vue) {
        return LocataireSituationResponseDto.builder()
                .locataireId(vue.getLocataireId())
                .nbBaux(vue.getNbBaux())
                .nbBauxActifs(vue.getNbBauxActifs())
                .nbQuittances(vue.getNbQuittances())
                .totalFacture(vue.getTotalFacture())
                .totalEncaisse(vue.getTotalEncaisse())
                .resteDu(vue.getResteDu())
                .nbQuittancesNonPayees(vue.getNbQuittancesNonPayees())
                .premierBail(vue.getPremierBail())
                .dernierPaiement(vue.getDernierPaiement())
                .build();
    }

    /**
     * Récupère la situation financière agrégée d'un locataire sur la totalité de ses baux.
     * Un locataire sans quittance obtient des compteurs et des montants à zéro ;
     * un locataire inexistant n'apparaît pas dans la vue et provoque une erreur 404.
     *
     * @param id l'identifiant du locataire
     * @return LocataireSituationResponseDto la situation financière du locataire
     * @throws NotFoundException si aucun locataire ne porte cet identifiant
     */
    public LocataireSituationResponseDto getLocataireStats(long id) {
        LocataireSituationView vue = locataireSituationRepository.findByLocataireId(id)
                .orElseThrow(() -> new NotFoundException(CrudService.NOT_FOUND_MESSAGE));
        return toDto(vue);
    }
}
