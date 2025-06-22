package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import org.springframework.stereotype.Service;

import java.util.SequencedCollection;

@Service
class VersionServiceImpl implements VersionService {

    private final SequencedCollection<Version> all;
    private final SequencedCollection<Version> published;

    VersionServiceImpl(SequencedCollection<Version> all, SequencedCollection<Version> published) {
        this.all = all;
        this.published = published;
    }

    @Override
    public SequencedCollection<Version> all() {
        return all;
    }

    @Override
    public SequencedCollection<Version> published() {
        return published;
    }

}
