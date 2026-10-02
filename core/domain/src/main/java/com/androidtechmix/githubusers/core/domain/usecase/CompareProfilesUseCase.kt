package com.androidtechmix.githubusers.core.domain.usecase

import com.androidtechmix.githubusers.core.model.LanguageCount
import com.androidtechmix.githubusers.core.model.ProfileComparison
import com.androidtechmix.githubusers.core.model.SharedLanguage
import com.androidtechmix.githubusers.core.model.UserDetail
import java.util.Locale
import javax.inject.Inject

/**
 * Pure comparison of two cached profiles.
 * Languages come from the top-repository list already stored with the profile.
 */
class CompareProfilesUseCase @Inject constructor() {

    operator fun invoke(left: UserDetail, right: UserDetail): ProfileComparison {
        val leftLanguages = languagesOf(left)
        val rightLanguages = languagesOf(right)
        val rightByKey = rightLanguages.associateBy { it.language.lowercase(Locale.ROOT) }
        val leftByKey = leftLanguages.associateBy { it.language.lowercase(Locale.ROOT) }

        val shared = leftLanguages.mapNotNull { leftLanguage ->
            val match = rightByKey[leftLanguage.language.lowercase(Locale.ROOT)] ?: return@mapNotNull null
            SharedLanguage(
                language = leftLanguage.language,
                leftCount = leftLanguage.repoCount,
                rightCount = match.repoCount,
            )
        }.sortedWith(sharedOrder)

        val leftOnly = leftLanguages.filter { it.language.lowercase(Locale.ROOT) !in rightByKey }
        val rightOnly = rightLanguages.filter { it.language.lowercase(Locale.ROOT) !in leftByKey }

        return ProfileComparison(
            shared = shared,
            leftOnly = leftOnly,
            rightOnly = rightOnly,
        )
    }

    private fun languagesOf(detail: UserDetail): List<LanguageCount> {
        return detail.repositories
            .mapNotNull { repo ->
                repo.language?.trim()?.takeIf { it.isNotEmpty() }
            }
            .groupBy { it.lowercase(Locale.ROOT) }
            .map { (_, names) ->
                LanguageCount(
                    language = names.first(),
                    repoCount = names.size,
                )
            }
            .sortedWith(countOrder)
    }

    private companion object {
        val countOrder: Comparator<LanguageCount> =
            compareByDescending<LanguageCount> { it.repoCount }
                .thenBy { it.language.lowercase(Locale.ROOT) }

        val sharedOrder: Comparator<SharedLanguage> =
            compareByDescending<SharedLanguage> { maxOf(it.leftCount, it.rightCount) }
                .thenBy { it.language.lowercase(Locale.ROOT) }
    }
}
