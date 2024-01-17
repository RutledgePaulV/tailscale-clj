(ns io.github.rutledgepaulv.tailscale.core
  (:require [clojure.data.json :as json]
            [clojure.java.shell :as sh]
            [io.github.rutledgepaulv.tailscale.fs :as fs]
            [clojure.set :as sets]
            [clojure.string :as strings]))

(defn tailscale-path []
  (let [path (System/getenv "PATH")
        os   (System/getProperty "os.name")
        home (System/getProperty "user.home")]
    (or (fs/which path "tailscale")
        (cond
          (.startsWith os "Mac")
          "/Applications/Tailscale.app/Contents/MacOS/Tailscale"
          (.startsWith os "Windows")
          (str home "\\AppData\\Local\\Tailscale\\tailscale.exe")
          (.startsWith os "Linux")
          (str home "/.local/share/Tailscale/tailscale")))))

(defn env []
  (into {"TAILSCALE_BE_CLI" "true"} (System/getenv)))

(defn tailscale [& args]
  (apply sh/sh (conj (into [(tailscale-path)] args) :env (env))))

(defn tailscale-status []
  (json/read-str (:out (tailscale "status" "--json"))))

(defn ensure-tag [s]
  (if (strings/starts-with? s "tag:") s (str "tag:" s)))

(defn tailscale-ips [& tags]
  (let [need (set (map ensure-tag tags))]
    (for [device (vals (get (tailscale-status) "Peer"))
          :let [has (set (get device "Tags"))]
          :when (sets/subset? need has)
          ip     (get device "TailscaleIPs")]
      ip)))