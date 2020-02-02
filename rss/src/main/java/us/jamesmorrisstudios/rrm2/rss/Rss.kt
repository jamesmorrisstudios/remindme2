package us.jamesmorrisstudios.rrm2.rss

/**
 * RSS handler.
 *
 * This manages downloading RSS feeds and returning the data to the caller.
 */
interface Rss {

    companion object {

        /**
         * The RSS handler instance.
         */
        val instance: Rss by lazy { RssImpl() }
    }

    // TODO build this out.
    // No state needs to be managed across launches. This is just a simple system to hit an RSS endpoint and download the data to present.

}

/**
 *
 */
private class RssImpl : Rss {




}