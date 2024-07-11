enum class Categorie(val categorie: String) {
    ACTOR("Actor"),
    SINGER("Singer"),
    DANCER("Dancer's"),
    INFLUENCER("Influencer's"),
    CASTINGCALLS("Casting Calls"),
    MUSICLABEL("Music Label"),
    CAMERALIGHT("Camera & Light"),
    LOCATIONMANAGER("Location Manager"),
    EVENTPLANNER("Event Planner"),
    PRODUCTIONHOUSE("Production Houses"),
    DJ("Dj's");

    override fun toString(): String {
        return categorie
    }
}