package com.ip2proxy

import java.nio.file.{Files, Paths}
import java.io.IOException
import munit.FunSuite

class IP2ProxyTest extends FunSuite {

  private val binfile = "IP2PROXY-LITE-PX1.BIN"
  private var binfilepath: String = _
  private val ip = "8.8.8.8"

  // Setup logic that runs once for the whole class
  override def beforeAll(): Unit = {
    val binpath = Paths.get("src", "test", "resources", binfile)
    if (!Files.exists(binpath)) {
      fail(s"BIN file not found at ${binpath.toAbsolutePath}. Please ensure it is in src/test/resources/")
    }
    binfilepath = binpath.toFile.getAbsolutePath
  }

  // Helper to manage the 'loc' lifecycle safely
  // This replaces 'before' and 'after' with a "Fixture"
  val proxyFixture = FunFixture[IP2Proxy](
    setup = { test =>
      val proxy = new IP2Proxy()
      proxy
    },
    teardown = { proxy =>
      proxy.Close
    }
  )

  test("TestOpenException") {
    val proxy = new IP2Proxy()
    // MUnit uses intercept instead of assertThrows
    intercept[IOException] {
      proxy.Open("dummy.bin")
    }

    intercept[NullPointerException] {
      proxy.Open(null.asInstanceOf[Array[Byte]])
    }
  }

  // Use the fixture for individual tests to ensure Close() is always called
  proxyFixture.test("TestQueryIsProxy") { proxy =>
    proxy.Open(binfilepath)
    val rec = proxy.GetAll(ip)
    assertEquals(rec.Is_Proxy, 0)
  }
}