
### Why 

Tailscale sidecars and zero trust networking is cool, but less discussed is that zero trust simply isn't very compatible
with server side load balancing. To achieve fault tolerance and high availability we either need to give up on zero trust
in some places (subnet routers / conventional load balancers) or we need a better solution for client side load balancing.

The first challenge for client side load balancing is always service discovery. We have a few options:

1) Ask Tailscale to implement a solution (perhaps magic DNS records that include all nodes matching a set of tags?)
2) Implement dynamic DNS records (option #1) ourselves using other DNS providers like Route53
3) Build a different (not DNS) service discovery mechanism that is Tailnet aware into our applications

This library implements option #3 and simply shells out to the Tailscale binary to query available Tailnet nodes.