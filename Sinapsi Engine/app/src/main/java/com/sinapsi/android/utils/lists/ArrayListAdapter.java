package com.sinapsi.android.utils.lists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Utility class to implement a RecyclerView adapter of a generic list of elements.
 */
public abstract class ArrayListAdapter<T> extends RecyclerView.Adapter<ArrayListAdapter.ItemViewHolder> implements List<T>{

    private ArrayList<T> arrayList;


    public ArrayListAdapter(){
        arrayList = new ArrayList<>();
    }

    public ArrayListAdapter(ArrayList<T> arrayList, Boolean addAll){
        if(addAll){
            this.arrayList = new ArrayList<>();
            this.arrayList.addAll(arrayList);
        }else{
            this.arrayList = arrayList;
        }
    }

    public ArrayListAdapter(Collection<? extends T> collection) {
        this.arrayList = new ArrayList<>(collection);
    }

    public ArrayListAdapter(int capacity){
        arrayList = new ArrayList<>(capacity);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(onCreateView(parent, viewType));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position){
        onBindViewHolder(holder, arrayList.get(position), position);
    }

    public abstract View onCreateView(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(ItemViewHolder viewHolder, T elem, int position);


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void add(int location, T object) {
        arrayList.add(location,object);
        notifyItemInserted(location);
    }

    @Override
    public boolean add(T object) {
        boolean result = arrayList.add(object);
        if(result) notifyItemInserted(size()-1);
        return result;

    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        boolean result = arrayList.addAll(location, collection);
        if(result) notifyDataSetChanged();
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean result = arrayList.addAll(collection);
        if(result) notifyDataSetChanged();
        return result;
    }

    @Override
    public void clear() {
        int prevSize = size();
        arrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean contains(Object object) {
        return arrayList.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return arrayList.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return arrayList.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return arrayList.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return arrayList.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return arrayList.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return arrayList.lastIndexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return arrayList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return arrayList.listIterator(location);
    }

    @Override
    public T remove(int location) {
        T result = arrayList.remove(location);
        notifyItemRemoved(location);
        notifyItemRangeChanged(location, arrayList.size());
        return result;
    }

    @Override
    public boolean remove(Object object) {
        boolean result = arrayList.remove(object);
        if(result) notifyDataSetChanged();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = arrayList.removeAll(collection);
        if(result) notifyDataSetChanged();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean result = arrayList.retainAll(collection);
        if(result) notifyDataSetChanged();
        return result;
    }

    @Override
    public T set(int location, T object) {
        T result = arrayList.set(location,object);
        notifyItemChanged(location);
        return result;
    }

    @Override
    public int size() {
        return arrayList.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return arrayList.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return arrayList.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] array) {
        return arrayList.toArray(array);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }



}
