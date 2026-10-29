package com.gestionLocative.gestionLocative.entity;

/** Cycle de vie d'un bail. Une valeur nulle en base (anciens baux) est assimilée à ACTIF. */
public enum StatutBail {
    ACTIF,
    RESILIE,
    TERMINE
}
