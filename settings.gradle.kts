rootProject.name = "fourthwall"

include(
    "3rdParty:omdbapi-client",
    "infrastructure:moviedb-client",
    "infrastructure:eventbus",
    "services:cinema-manager",
    "services:movies-listing"
)
