package demoparser
package serialization

import model.Demo

trait DemoSerializer {
  def serialize(demo: Demo): String
}
