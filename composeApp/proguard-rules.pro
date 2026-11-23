# Compose, Ktor, kotlinx-serialization and Koin all ship their own consumer ProGuard/R8 rules
# inside their artifacts, which AGP merges in automatically — this file only adds what's specific
# to this app. Start empty; add keep rules here if a release build crashes on something R8
# stripped or renamed that a library's own consumer rules didn't cover (most often reflection over
# an @Serializable class, or a Koin-injected type).
