package fr.gouv.agriculture.ift.util;

public interface Views {

    interface Public {
    }

    interface ExtendedPublic extends Public {
    }

    interface Internal extends ExtendedPublic {
    }
}
