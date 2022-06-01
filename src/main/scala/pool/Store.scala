package pool

import scalikejdbc.*

final class Store(context: Context):
  ConnectionPool.singleton(DataSourceConnectionPool(context.dataSource))

  def pools(): List[Pool] = DB readOnly { implicit session =>
    sql"select * from pool order by built desc"
      .map(rs => Pool(rs.long("id"), rs.string("name"), rs.localDate("built"), rs.int("volume")))
      .list()
  }

  def add(pool: Pool): Pool = DB localTx { implicit session =>
    val id = sql"insert into pool(name, built, volume) values(${pool.name}, ${pool.built}, ${pool.volume})"
      .updateAndReturnGeneratedKey()
    pool.copy(id = id)
  }

  def update(pool: Pool): Unit = DB localTx { implicit session =>
    sql"update pool set name = ${pool.name}, built = ${pool.built}, volume = ${pool.volume} where id = ${pool.id}"
      .update()
  }

  def freeChlorines(): List[FreeChlorine] = DB readOnly { implicit session =>
    sql"select * from free_chlorine order by date_measured, time_measured"
      .map(rs => FreeChlorine(
        rs.long("id"),
        rs.long("pool_id"), 
        rs.localDate("date_measured"), 
        rs.localTime("time_measured"), 
        rs.double("measurement")))
      .list()
  }

  def add(freeChlorine: FreeChlorine): FreeChlorine = DB localTx { implicit session =>
    val id = sql"""insert into free_chlorine(pool_id, date_measured, time_measured, measurement)" +
      values(${freeChlorine.poolId}, ${freeChlorine.dateMeasured}, ${freeChlorine.timeMeasured}, ${freeChlorine.measurement})"""
      .updateAndReturnGeneratedKey()
    freeChlorine.copy(id = id)
  }

  def update(freeChlorine: FreeChlorine): Unit = ()

  def combinedChlorines(): List[CombinedChlorine] = List[CombinedChlorine]()
  def add(combinedChlorine: CombinedChlorine): Int = 0
  def update(combinedChlorine: CombinedChlorine): Unit = ()

  def totalChlorines(): List[TotalChlorine] = List[TotalChlorine]()
  def add(totalChlorine: TotalChlorine): Int = 0
  def update(totalChlorine: TotalChlorine): Unit = ()

  def pHs(): List[pH] = List[pH]()
  def add(pH: pH): Int = 0
  def update(pH: pH): Unit = ()

  def calciumHardnesses(): List[CalciumHardness] = List[CalciumHardness]()
  def add(calciumHardness: CalciumHardness): Int = 0
  def update(calciumHardness: CalciumHardness): Unit = ()

  def totalAlkalinities(): List[TotalAlkalinity] = List[TotalAlkalinity]()
  def add(totalAlkalinity: TotalAlkalinity): Int = 0
  def update(totalAlkalinity: TotalAlkalinity): Unit = ()

  def cyanuricAcids(): List[CyanuricAcid] = List[CyanuricAcid]()
  def add(cyanuricAcid: CyanuricAcid): Int = 0
  def update(cyanuricAcid: CyanuricAcid): Unit = ()

  def totalBromines(): List[TotalBromine] = List[TotalBromine]()
  def add(totalBromine: TotalBromine): Int = 0
  def update(totalBromine: TotalBromine): Unit = ()

  def temperatures(): List[Temperature] = List[Temperature]()
  def add(temperature: Temperature): Int = 0
  def update(temperature: Temperature): Unit = ()

  def liquidChlorines(): List[LiquidChlorine] = List[LiquidChlorine]()
  def add(liquidChlorine: LiquidChlorine): Int = 0
  def update(liquidChlorine: LiquidChlorine): Unit = ()

  def trichlors(): List[Trichlor] = List[Trichlor]()
  def add(trichlor: Trichlor): Int = 0
  def update(trichlor: Trichlor): Unit = ()

  def dichlors(): List[Dichlor] = List[Dichlor]()
  def add(dichlor: Dichlor): Int = 0
  def update(dichlor: Dichlor): Unit = ()

  def calciumHypochlorites(): List[CalciumHypochlorite] = List[CalciumHypochlorite]()
  def add(calciumHypochlorite: CalciumHypochlorite): Int = 0
  def update(calciumHypochlorite: CalciumHypochlorite): Unit = ()

  def stabilizers(): List[Stabilizer] = List[Stabilizer]()
  def add(stabilizer: Stabilizer): Int = 0
  def update(stabilizer: Stabilizer): Unit = ()

  def algaecides(): List[Algaecide] = List[Algaecide]()
  def add(algaecide: Algaecide): Int = 0
  def update(algaecide: Algaecide): Unit = ()