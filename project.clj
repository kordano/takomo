(defproject takomo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 #_[org.clojure/tools.reader "1.2.2"]
                 [io.replikativ/datahike "0.2.0-beta4"]
                 [io.replikativ/hasch "0.3.6-SNAPSHOT"]
                 [metosin/reitit "0.3.9"]
                 [http-kit "2.3.0"]
                 [metosin/muuntaja "0.6.4"]
                 [clj-time "0.15.1"]]
  :repl-options {:init-ns takomo.core})
