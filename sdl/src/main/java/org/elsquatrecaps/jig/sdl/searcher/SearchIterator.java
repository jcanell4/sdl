/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.Iterator;

/**
 *
 * @author josep
 */
public abstract class SearchIterator<T extends SearcherResource> implements Iterator<T>{
    public abstract void init(AbstractGetRemoteProcess getRemoteProcess);
}
