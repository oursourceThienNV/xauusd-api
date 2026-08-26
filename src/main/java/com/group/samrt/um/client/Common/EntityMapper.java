package com.group.samrt.um.client.Common;

import java.util.List;

public interface EntityMapper<D, E> {
    E toEntity(D var1);

    D toDto(E var1);

    List<E> toEntity(List<D> var1);

    List<D> toDto(List<E> var1);
}