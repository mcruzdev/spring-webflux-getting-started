package com.github.mcruzdev.plan;

import org.springframework.web.util.UriComponentsBuilder;

public class PaginationSupport {

    private final Data data;

    public PaginationSupport(Data data) {
        this.data = data;
    }

    public String getLinkHeader() {

        int page = this.data.page;
        int totalPages = Math.toIntExact(this.data.count / this.data.size);
        int pageSize = this.data.size;
        var uriBuilder = UriComponentsBuilder.fromPath(this.data.path);

        final StringBuilder linkHeader = new StringBuilder();

        if (hasNextPage(page, totalPages)) {
            final String uriForNextPage = constructNextPageUri(uriBuilder, page, pageSize);
            linkHeader.append(createLinkHeader(uriForNextPage, "next"));
        }

        if (hasPreviousPage(page)) {
            final String uriForPrevPage = constructPrevPageUri(uriBuilder, page, pageSize);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForPrevPage, "prev"));
        }

        if (hasFirstPage(page)) {
            final String uriForFirstPage = constructFirstPageUri(uriBuilder, pageSize);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForFirstPage, "first"));
        }

        if (hasLastPage(page, totalPages)) {
            final String uriForLastPage = constructLastPageUri(uriBuilder, totalPages, pageSize);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForLastPage, "last"));
        }

        return linkHeader.toString();
    }

    String constructNextPageUri(final UriComponentsBuilder uriBuilder, final int page, final int size) {
        return uriBuilder.replaceQueryParam("page", page + 1).replaceQueryParam("size", size).build().encode().toUriString();
    }

    String constructPrevPageUri(final UriComponentsBuilder uriBuilder, final int page, final int size) {
        return uriBuilder.replaceQueryParam("page", page - 1).replaceQueryParam("size", size).build().encode().toUriString();
    }

    String constructFirstPageUri(final UriComponentsBuilder uriBuilder, final int size) {
        return uriBuilder.replaceQueryParam("page", 0).replaceQueryParam("size", size).build().encode().toUriString();
    }

    String constructLastPageUri(final UriComponentsBuilder uriBuilder, final int totalPages, final int size) {
        return uriBuilder.replaceQueryParam("page", totalPages).replaceQueryParam("size", size).build().encode().toUriString();
    }

    boolean hasNextPage(final int page, final int totalPages) {
        return page < totalPages - 1;
    }

    boolean hasPreviousPage(final int page) {
        return page > 0;
    }

    boolean hasFirstPage(final int page) {
        return hasPreviousPage(page);
    }

    boolean hasLastPage(final int page, final int totalPages) {
        return totalPages > 1 && hasNextPage(page, totalPages);
    }

    void appendCommaIfNecessary(final StringBuilder linkHeader) {
        if (linkHeader.length() > 0) {
            linkHeader.append(", ");
        }
    }

    public static String createLinkHeader(final String uri, final String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }

    public static class Data {

        private String path;
        private int count;
        private int size;
        private int page;

        private Data(String path, int count, int size, int page) {
            this.path = path;
            this.count = count;
            this.size = size;
            this.page = page;
        }

        public static Data of(final String path, final int count, final int size, final int page) {
            return new Data(path, count, size, page);
        }

        public String getPath() {
            return path;
        }

        public int getCount() {
            return count;
        }

        public int getSize() {
            return size;
        }

        public int getPage() {
            return page;
        }
    }
}
