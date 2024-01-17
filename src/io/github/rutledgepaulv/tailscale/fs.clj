(ns io.github.rutledgepaulv.tailscale.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as strings])
  (:import (java.nio.file LinkOption)
           (java.io File)))

(def link-0 (into-array LinkOption []))

(defn is-real-directory? [^File file]
  (and (.exists file) (.isDirectory file)))

(defn is-executable-binary? [^File file]
  (and (.isFile file) (.canExecute file)))

(defn ensure-resolved [^File file]
  (.toFile (.toRealPath (.toPath file) link-0)))

(defn which
  ([binary]
   (which (System/getenv "PATH") binary))
  ([path binary]
   (->> (strings/split (or path "") #":")
        (map io/file)
        (filter is-real-directory?)
        (map ensure-resolved)
        (distinct)
        (mapcat file-seq)
        (distinct)
        (filter is-executable-binary?)
        (reduce (fn [nf x] (if (= binary (.getName x)) (reduced (.getAbsolutePath x)) nf)) nil))))
