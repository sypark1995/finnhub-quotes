package com.sypark.finnhub.core.domain.usecase.search

/**
 * Finnhub's /search endpoint only indexes English company names and tickers,
 * so a Korean query like "엔비디아" returns zero results. This curated
 * dictionary translates well-known Korean stock names to the ticker Finnhub
 * understands before the query is sent.
 */
internal object KoreanStockAliases {
    private val aliases: Map<String, String> = mapOf(
        "애플" to "AAPL",
        "마이크로소프트" to "MSFT",
        "엔비디아" to "NVDA",
        "구글" to "GOOGL",
        "알파벳" to "GOOGL",
        "아마존" to "AMZN",
        "메타" to "META",
        "페이스북" to "META",
        "테슬라" to "TSLA",
        "넷플릭스" to "NFLX",
        "인텔" to "INTC",
        "퀄컴" to "QCOM",
        "브로드컴" to "AVGO",
        "어드밴스드마이크로디바이스" to "AMD",
        "에이엠디" to "AMD",
        "페이팔" to "PYPL",
        "디즈니" to "DIS",
        "코카콜라" to "KO",
        "스타벅스" to "SBUX",
        "나이키" to "NKE",
        "맥도날드" to "MCD",
        "화이자" to "PFE",
        "존슨앤존슨" to "JNJ",
        "비자" to "V",
        "마스터카드" to "MA",
        "제이피모건" to "JPM",
        "뱅크오브아메리카" to "BAC",
        "버크셔해서웨이" to "BRK.B",
        "어도비" to "ADBE",
        "세일즈포스" to "CRM",
        "오라클" to "ORCL",
        "시스코" to "CSCO",
        "아이비엠" to "IBM",
        "보잉" to "BA",
        "월마트" to "WMT",
        "홈디포" to "HD",
        "우버" to "UBER",
        "스포티파이" to "SPOT",
        "에어비앤비" to "ABNB",
        "코인베이스" to "COIN",
        "팔란티어" to "PLTR",
    )

    /** Returns the ticker for [query] if it matches a known Korean alias, else null. */
    fun resolve(query: String): String? {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return null
        aliases[trimmed]?.let { return it }
        return aliases.entries.firstOrNull { (name, _) -> name.contains(trimmed) }?.value
    }
}
