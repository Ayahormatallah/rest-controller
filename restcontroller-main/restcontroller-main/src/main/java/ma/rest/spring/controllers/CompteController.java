package ma.rest.spring.controllers;

import ma.rest.spring.entities.Compte;
import ma.rest.spring.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/banque")
public class CompteController {

    @Autowired
    private CompteRepository compteRepository;

    // READ: Récupérer tous les comptes (JSON et XML)
    @GetMapping(value = "/comptes", produces = { "application/json", "application/xml" })
    public List<Compte> getAllComptes() {
        return compteRepository.findAll();
    }

    // READ: Récupérer un compte par son identifiant (JSON et XML)
    @GetMapping(value = "/comptes/{id}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Compte> getCompteById(@PathVariable Long id) {
        return compteRepository.findById(id)
                .map(ResponseEntity::ok) // Renvoie un "200 OK" avec le compte trouvé
                .orElse(ResponseEntity.notFound().build()); // Renvoie un "404 Not Found" si non trouvé
    }

    // CREATE: Ajouter un nouveau compte (JSON et XML)
    @PostMapping(value = "/comptes", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
    public ResponseEntity<Compte> createCompte(@Valid @RequestBody Compte compte) {
        Compte savedCompte = compteRepository.save(compte);
        return ResponseEntity.ok(savedCompte);
    }

    // UPDATE: Mettre à jour un compte existant (JSON et XML)
    @PutMapping(value = "/comptes/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
    public ResponseEntity<Compte> updateCompte(@PathVariable Long id, @Valid @RequestBody Compte compteDetails) {
        return compteRepository.findById(id)
                .map(compte -> {
                    // Mise à jour des champs du compte existant
                    compte.setSolde(compteDetails.getSolde());
                    compte.setDateCreation(compteDetails.getDateCreation());
                    compte.setType(compteDetails.getType());
                    Compte updatedCompte = compteRepository.save(compte); // Enregistrement des modifications
                    return ResponseEntity.ok(updatedCompte);
                })
                .orElse(ResponseEntity.notFound().build()); // Si l'ID n'existe pas, renvoyer 404
    }

    // DELETE: Supprimer un compte
    @DeleteMapping("/comptes/{id}")
    public ResponseEntity<Void> deleteCompte(@PathVariable Long id) {
        return compteRepository.findById(id)
                .map(compte -> {
                    compteRepository.delete(compte); // Supprimer le compte si trouvé
                    return ResponseEntity.ok().<Void>build(); // Retourner 200 OK sans contenu
                })
                .orElse(ResponseEntity.notFound().build()); // Retourner 404 si l'ID n'existe pas
    }

    // Gestion globale des exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception ex) {
        ex.printStackTrace(); // Log de l'exception dans la console
        return ResponseEntity.status(500).body("Une erreur est survenue : " + ex.getMessage());
    }
}
