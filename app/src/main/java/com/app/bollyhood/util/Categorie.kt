enum class Categorie(val categorie: String) {
    ACTOR("Actors"),
    SINGER("Singers"),
    DANCER("Dancer"),
    INFLUENCER("Influencers"),
    CASTINGCALLS("Casting Calls"),
    MUSICLABEL("Music Producer"),
    CAMERALIGHT("Camera & Lights"),
    LOCATIONMANAGER("Shoot Location"),
    EVENTPLANNER("Event Planners"),
    PRODUCTIONHOUSE("Production Houses"),
    CAMERANLIGHTS("Camera & Lights"),
    DJ("Dj's");

    override fun toString(): String {
        return categorie
    }
}