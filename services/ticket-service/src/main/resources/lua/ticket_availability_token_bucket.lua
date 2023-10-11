--# KEYS[2] 就是咱们用户购买的出发站点和到达站点，比如北京南_南京南

local inputString = KEYS[2]
local actualKey = inputString

--# 因为 Redis Key 序列化器的问题，会把 mading: 给加上
--# 所以这里需要把 mading: 给删除，仅保留北京南_南京南
--# actualKey 就是北京南_南京南
local colonIndex = string.find(actualKey, ":")
if colonIndex ~= nil then
    actualKey = string.sub(actualKey, colonIndex + 1)
end
--# ARGV[1] 是需要扣减的座位类型以及对应数量
local jsonArrayStr = ARGV[1]

--# 因为传递过来是 JSON 字符串，所以这里再序列化成对象
local jsonArray = cjson.decode(jsonArrayStr)


--# 遍历 jsonArray，获取座位类型以及对应数量
for index, jsonObj in ipairs(jsonArray) do
    --# 座位类型
    local seatType = tonumber(jsonObj.seatType)
    --# 座位数量
    local count = tonumber(jsonObj.count)
    --# 拼接成北京南_南京南_座位类型
    local actualInnerHashKey = actualKey .. "_" .. seatType
    --# 对比指定token余量 和 需要扣减的座位数量
    local ticketSeatAvailabilityTokenValue = tonumber(redis.call('hget', KEYS[1], tostring(actualInnerHashKey)))
    if ticketSeatAvailabilityTokenValue < count then
        return 1
    end
end

--# 通过上面的判断，已经知道令牌容器中对应的出发站点和到达站点对应的座位类型余票充足，开始进行扣减
--# ARGV[2] 是需要扣减的相关列车站点
local alongJsonArrayStr = ARGV[2]
--# 因为传递过来是 JSON 字符串，所以这里再序列化成对象
local alongJsonArray = cjson.decode(alongJsonArrayStr)

--# 遍历 jsonArray 是需要扣减的座位类型以及对应数量
for index, jsonObj in ipairs(jsonArray) do
    --# 座位类型
    local seatType = tonumber(jsonObj.seatType)
    --# 座位数量
    local count = tonumber(jsonObj.count)
    --# 遍历 alongJsonArray 是需要扣减的相关列车站点
    for indexTwo, alongJsonObj in ipairs(alongJsonArray) do
        local startStation = tostring(alongJsonObj.startStation)
        local endStation = tostring(alongJsonObj.endStation)
        local actualInnerHashKey = startStation .. "_" .. endStation .. "_" .. seatType
        redis.call('hincrby', KEYS[1], tostring(actualInnerHashKey), -count)
    end
end

return 0
