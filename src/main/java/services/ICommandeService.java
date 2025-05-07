package services;

import entities.GestionVente.Commande;

import java.util.List;

public interface ICommandeService<T> {

        List<Commande> getAll();
        Commande getById(int id);
    }
