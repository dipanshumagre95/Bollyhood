enum class Categorie(val categorie: String) {
    ACTOR("Actor"),
    SINGER("Singer"),
    DANCER("Dancer's"),
    INFLUENCER("Influencer's"),
    DJ("Dj's");

    override fun toString(): String {
        return categorie
    }
}