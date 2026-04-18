package net.meshpeak.taiju.data.local.entity

import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry

fun WeightEntryEntity.toDomain(): WeightEntry =
    WeightEntry(
        id = id,
        date = date,
        weightKg = weightKg,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun WeightEntry.toEntity(): WeightEntryEntity =
    WeightEntryEntity(
        id = id,
        date = date,
        weightKg = weightKg,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun MemoEntity.toDomain(): Memo =
    Memo(
        id = id,
        date = date,
        content = content,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Memo.toEntity(): MemoEntity =
    MemoEntity(
        id = id,
        date = date,
        content = content,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
